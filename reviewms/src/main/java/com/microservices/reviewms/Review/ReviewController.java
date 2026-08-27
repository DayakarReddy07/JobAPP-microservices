package com.microservices.reviewms.Review;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviews(@RequestParam Long companyId) {
       return new ResponseEntity<>(reviewService.getAllReviews(companyId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addReview(@RequestParam Long companyId, @RequestBody Review review) {
        boolean isReview = reviewService.addReview(companyId, review);
        if(isReview) {
            return new ResponseEntity<>("Review added successfully", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Review could not be added", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable Long reviewId) {
        Review review =  reviewService.getReview(reviewId);
        if(review != null) {
            return new ResponseEntity<>(review, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Review not found with this Id", HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("/update/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId, @RequestBody Review review) {
        boolean isReview = reviewService.updateReview(reviewId, review);
        if(isReview) {
            return new ResponseEntity<>("Review updated successfully", HttpStatus.OK);
        }
        return new  ResponseEntity<>("Review NOT UPDATED", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        boolean isReviewDeleted = reviewService.deleteReview(reviewId);
        if(isReviewDeleted) {
            return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Review NOT DELETED", HttpStatus.NOT_FOUND);
        }
    }
}
