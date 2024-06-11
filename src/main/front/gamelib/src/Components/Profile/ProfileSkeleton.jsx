import React from "react";


const ProfileSkeleton = () => {
    return (
        <div>
            <div className='relative'>
                <div className='flex flex-col md:flex-row'>
                    <div className='flex-grow'>
                        {/* Banner */}
                        <div className='bg-white relative border-2 '>
                            <div className="animate-pulse">
                                <div className="w-full h-[250px] bg-gray-200"></div>
                            </div>
                        </div>
                        {/* Profile Information */}
                        <div className="flex w-4/5 md:w-3/4 lg:w-1/2 h-auto items-center mx-auto md:mx-16 z-40 -mt-32 rounded-lg p-4">
                            <div className="relative h-52 w-52 md:h-56 md:w-56 bg-gray-200 animate-pulse rounded-full z-1000"></div>
                            <div className="ml-4 pt-20">
                                <div className="h-8 w-3/4 bg-gray-200 animate-pulse mb-4"></div>
                                <div className="h-6 w-1/2 bg-gray-200 animate-pulse mb-4"></div>
                                <div className="h-4 w-full bg-gray-200 animate-pulse"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ProfileSkeleton;
