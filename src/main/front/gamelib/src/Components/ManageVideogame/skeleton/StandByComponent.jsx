import React from "react";
import styled, {keyframes} from 'styled-components';

const shimmer = keyframes`
    0% {
        background-position: -100em 0;
    }
    100% {
        background-position: 100em 0;
    }
`;

const SkeletonContainer = styled.div`
    width: 100%;
    height: 2em;
    background: linear-gradient(90deg, #eeffe2, #ffa127);
    border: 0.35em solid grey;
    animation: ${shimmer} 5s linear infinite;
`;

const StandByComponent = () => (
    <SkeletonContainer />
);

export default StandByComponent;
