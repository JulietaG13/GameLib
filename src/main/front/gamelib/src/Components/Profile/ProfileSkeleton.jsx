import React from "react";
import skeletoncss from "../TheLibrary/skeletons/SkeletonLoader.css";


const ProfileSkeleton = () => {
    return (
        <div className={"animate-pulse -mt-8"}>
            <div className="flex items-center justify-center h-screen">
                <div className="w-full px-4">
                    <div className="space-y-4">
                        <div className="h-[250px] bg-gray-300 rounded "></div>
                        <div className="flex ">
                            <div
                                className="relative h-52 w-52 md:h-56 md:w-56 bg-gray-300  rounded-full z-1000 -mt-40 ml-6 border border-gray-400"></div>
                            <div className="flex-1 h-20 bg-gray-300 rounded"></div>
                        </div>
                        <div className="h-8 bg-gray-300 rounded mt-10"></div>
                        <div className="flex flex-wrap justify-between gap-4">
                            {Array(6).fill().map((_, index) => (
                                <div key={index} className="flex-1 bg-gray-300 rounded-lg "
                                     style={{height: '350px', width: 'calc(20% - 10px)'}}></div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ProfileSkeleton;
