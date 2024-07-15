import React from 'react';
import ReviewItem from './ReviewItem';
import './ReviewsList.css';

const ReviewsList = ({ reviews }) => {
    return (
        <div>
            {reviews.map((review) => (
                <ReviewItem key={review.id} review={review} />
            ))}
        </div>
    );
};

export default ReviewsList;
