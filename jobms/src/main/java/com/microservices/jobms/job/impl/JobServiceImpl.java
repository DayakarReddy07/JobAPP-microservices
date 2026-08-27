package com.microservices.jobms.job.impl;


import com.microservices.jobms.job.DTO.JobDTO;
import com.microservices.jobms.job.Job;
import com.microservices.jobms.job.JobRepository;
import com.microservices.jobms.job.JobService;
import com.microservices.jobms.job.external.Company;
import com.microservices.jobms.job.external.Review;
import com.microservices.jobms.job.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {
//    private List<Job> jobs = new ArrayList<>();

    private final JobRepository jobRepository;

    @Autowired
    RestTemplate restTemplate;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<JobDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream().map(this:: convertTODTO).collect(Collectors.toList());
    }

    private JobDTO convertTODTO(Job job) {
//        RestTemplate restTemplate = new RestTemplate();
        Company company = restTemplate.getForObject("http://COMPANY-SERVICE:8081/companies/"+job.getCompanyId(), Company.class);
        ResponseEntity<List<Review>> reviewResponse = restTemplate.exchange("http://REVIEW-SERVICE:8083/reviews?companyId=" + job.getCompanyId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Review>>() {
        });
        List<Review> reviews = reviewResponse.getBody();
        return JobMapper.mapToJobWithCompanyDTO(job,company,reviews);
    }

    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
       return convertTODTO(job);
    }

    @Override
    public boolean deleteJobById(Long id) {
        try{
            jobRepository.deleteById(id);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    @Override
    public boolean updateJobById(Long id, Job updatedJob) {
        Optional<Job> jobOptional = jobRepository.findById(id);
           if(jobOptional.isPresent()) {
               Job job = jobOptional.get();
               job.setTitle(updatedJob.getTitle());
               job.setDescription(updatedJob.getDescription());
               job.setLocation(updatedJob.getLocation());
               job.setMaxSalary(updatedJob.getMaxSalary());
               job.setMinSalary(updatedJob.getMinSalary());
               jobRepository.save(job);
               return true;
           }
           return false;
    }
}
