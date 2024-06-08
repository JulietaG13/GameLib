// SkeletonLoader.js
import React from 'react';
import './SkeletonLoader.css'; // Estilos CSS para el esqueleto

const SkeletonLoader = () => {
    return (
        <div className="skeleton-library">
            <div className="skeleton-content">
                <div className="skeleton-genres">
                    <div className="skeleton-genre-title"></div>
                    {Array(10).fill().map((_, index) => (
                        <div key={index} className="skeleton-genre-item"></div>
                    ))}
                </div>
                <div className="skeleton-main">
                    <div className="skeleton-banner"></div>
                    <div className="skeleton-trending-title"></div>
                    <div className="skeleton-trending-games">
                        {Array(6).fill().map((_, index) => (
                            <div key={index} className="skeleton-trending-item"></div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SkeletonLoader;
