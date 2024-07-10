import React from "react";
import styled, {keyframes} from 'styled-components';

const shimmer = keyframes`
    0% {
        background-position: -1000px 0;
    }
    100% {
        background-position: 1000px 0;
    }
`;

const SkeletonContainer = styled.div`
    width: 100%;
    height: 500px; 
    background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
    background-size: 200% 100%;
    animation: ${shimmer} 1.5s infinite;
    border-radius: 0.75rem;
`;

const SkeletonComp = () => (
    <SkeletonContainer />
);

export default SkeletonComp;
