import React from 'react';
import { Link } from 'react-router-dom';
import user_icon from "../../Assets/user-icon.png";
import './ReviewItem.css';

const ReviewItem = ({ review }) => {
    return (
        <div className="reviewDiv">
            <Link className="profileLink userIconContainer"
                  to={'/profile/' + review.author.username}
                  title={`Visit ${review.author.username}'s page!`}
            >
                <img src={review.author.pfp !== null ? review.author.pfp : user_icon} alt="user_icon" className="userIcon" />
            </Link>
            <div className="reviewContent">
                <Link className="profileLink"
                      to={'/profile/' + review.author.username}
                      title={`Visit ${review.author.username}'s page!`}
                >
                    <p className="username">{review.author.username}</p>
                </Link>
                <p className="reviewText">{review.text}</p>
            </div>
        </div>
    );
};

export default ReviewItem;
