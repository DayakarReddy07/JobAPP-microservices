package com.microservices.jobms.job.impl;


import com.microservices.jobms.job.DTO.JobDTO;
import com.microservices.jobms.job.Job;
import com.microservices.jobms.job.JobRepository;
import com.microservices.jobms.job.JobService;
import com.microservices.jobms.job.clients.CompanyClient;
import com.microservices.jobms.job.clients.ReviewClient;
import com.microservices.jobms.job.external.Company;
import com.microservices.jobms.job.external.Review;
import com.microservices.jobms.job.mapper.JobMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {
//    private List<Job> jobs = new ArrayList<>();

    private final JobRepository jobRepository;
    private final CompanyClient companyClient;
    private final ReviewClient reviewClient;
    int attempt = 0;

    @Autowired
    RestTemplate restTemplate;

    public JobServiceImpl(JobRepository jobRepository, CompanyClient companyClient, ReviewClient reviewClient) {
        this.jobRepository = jobRepository;
        this.companyClient = companyClient;
        this.reviewClient = reviewClient;
    }

    @Override
//    @CircuitBreaker(name="companyBreaker",fallbackMethod="companyBreakerFallback")
//    @Retry(name="companyBreaker", fallbackMethod="companyBreakerFallback")
    @RateLimiter(name="companyBreaker", fallbackMethod="companyBreakerFallback")
    public List<JobDTO> findAll() {
        System.out.println("Attempt: " + ++attempt);
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream().map(this:: convertTODTO).collect(Collectors.toList());
    }

    public List<String> companyBreakerFallback(Exception E){
        List<String> list = new ArrayList<>();
        list.add("Dummy");
        return list;
    }

    private JobDTO convertTODTO(Job job) {
//        RestTemplate restTemplate = new RestTemplate();
        Company company = companyClient.getCompany(job.getCompanyId());
        List<Review> reviews = reviewClient.getReviews(job.getId());
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
