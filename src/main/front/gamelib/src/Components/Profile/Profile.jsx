import React, { useEffect, useState } from "react";
import gamelib_logo from "../Assets/Designer(3).jpeg";
import userProfile from "../Assets/user-icon.png";
import { useParams, useNavigate, Navigate, useLocation } from "react-router-dom";
import axios from "axios";
import Header from "../Header/Header";
import Shelves from "./Shelves";
import ProfileSkeleton from "./ProfileSkeleton"; // Import the skeleton component
import Alert from "../alert/Alert";
import PayPopup from "../Payment/PayPopup";
import StatusPopup from '../Payment/StatusPopup';

function Profile() {
    const navigate = useNavigate();
    const { username } = useParams();
    const [usernameResponse, setUsernameResponse] = useState('');
    const [description, setDescription] = useState('');
    const [notFound, setNotFound] = useState(false);
    const [isLoading, setIsLoading] = useState(true); // State to track loading
    const [isFriend, setIsFriend] = useState(false);
    const [isPending, setIsPending] = useState(false); // State to track if the friend request is pending
    const [isFollowing, setIsFollowing] = useState(false); // State to track follow status
    const [isBanned, setIsBanned] = useState(false); // State to track if the user is banned
    const [profilePicture, setProfilePicture] = useState(userProfile); // State for profile picture
    const [bannerImage, setBannerImage] = useState(gamelib_logo); // State for banner image
    const loggedInUsername = localStorage.getItem('username');
    const [alert, setAlert] = useState({ message: '', visible: false, position: { top: 0, left: 0 } }); // State for alert

    // payment
    const location = useLocation();
    const [isDonationsSetup, setIsDonationsSetup] = useState(null);
    const [status, setStatus] = useState(null);
    const [preferenceId, setPreferenceId] = useState(null);
    const [showPopup, setShowPopup] = useState(false);

    const handleAdminAction = () => {
        axios.put('http://localhost:4567/admin/ban/' + localStorage.getItem("currentProfileId"), {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        }).then(() => {
            console.log("User banned successfully");
            setIsBanned(true); // Update state to indicate the user is banned
        }).catch(e => {
            console.error('Error:', e);
            showAlert('Failed to ban user. Please try again.');
        })
    };

    const validateLogin = async () => {
        try {
            await axios.post('http://localhost:4567/tokenvalidation', {}, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token')
                }
            });
            return true;
        } catch (error) {
            console.error('Error validating login:', error);
            return false;
        }
    };

    const handleUnbanUser = () => {
        axios.put('http://localhost:4567/admin/unban/' + localStorage.getItem("currentProfileId"), {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        }).then(() => {
            console.log("User unbanned successfully");
            setIsBanned(false); // Update state to indicate the user is unbanned
        }).catch(e => {
            console.error('Error:', e);
            showAlert('Failed to unban user. Please try again.');
        })
    };

    const handleFollow = async () => {
        const isLoggedIn = await validateLogin();
        if (!isLoggedIn) {
            showAlert("You need to be logged in to perform this action.");
            return;
        }

        const endpoint = isFollowing ? 'unsubscribe' : 'subscribe';
        axios.post(`http://localhost:4567/dev/subs/${endpoint}/${localStorage.getItem("currentProfileId")}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() => {
                setIsFollowing(!isFollowing); // Toggle the follow status
            })
            .catch(error => {
                console.error(`Error ${isFollowing ? 'unfollowing' : 'following'} developer:`, error);
                showAlert(`Failed to ${isFollowing ? 'unfollow' : 'follow'} developer. Please try again.`);
            });
    };

    const showAlert = (message) => {
        setAlert({
            message,
            visible: true,
            position: { top: window.innerHeight / 2 - 50, left: window.innerWidth / 2 - 150 }
        });
        setTimeout(() => setAlert({ ...alert, visible: false }), 6000);
    };

    useEffect(() => {
        if (loggedInUsername !== username) {
            axios.get(`http://localhost:4567/user/friends/status/${localStorage.getItem("currentProfileId")}`, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token')
                }
            })
                .then(response => {
                    console.log(response.data);
                    setIsFriend(response.data.is_friend);
                    setIsPending(response.data.is_sent);
                })
                .catch(error => {
                    console.error("Error checking friendship status:", error);
                });
        }
    }, [username, loggedInUsername]);

    useEffect(() => {
        if (loggedInUsername !== username) {
            axios.get(`http://localhost:4567/dev/subs/is/${localStorage.getItem("currentProfileId")}`, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token')
                }
            })
                .then(response => {
                    console.log(response.data);
                    setIsFollowing(response.data.is_subscribed);
                })
                .catch(error => {
                    console.error("Error checking following status:", error);
                });
        }
    }, [username, loggedInUsername]);

    useEffect(() => {
        axios.get(`http://localhost:4567/getprofile/${username}`)
            .then(response => {
                console.log(response.data);

                setIsBanned(response.data.is_banned);

                localStorage.setItem('currentProfileRol', response.data.rol);
                localStorage.setItem('currentProfileId', response.data.id);
                setUsernameResponse(response.data.username);
                setDescription(response.data.biography);
                setProfilePicture(response.data.pfp || userProfile);
                setBannerImage(response.data.banner || gamelib_logo);
                setIsLoading(false); // Set loading to false once data is fetched
            })
            .catch(() => {
                setNotFound(true);
            });
    }, [username]);

    // payment
    useEffect(() => {
        axios.get(`http://localhost:4567/pay/setup/is/${username}`)
            .then(response => {
                const isSetup = response.data.is_setup;
                setIsDonationsSetup(isSetup);
            })
            .catch(() => {
                setIsDonationsSetup(false);
            });
    }, [username])

    useEffect(() => {
        const searchParams = new URLSearchParams(location.search);
        const statusParam = searchParams.get('status');
        const preferenceIdParam = searchParams.get('preference_id');

        if (statusParam) {
            setStatus(statusParam);
            setPreferenceId(preferenceIdParam);
            setShowPopup(true);
        }
    }, [location]);

    const handleClosePopup = () => {
        setShowPopup(false);
        navigate('/profile/' + username);
    };

    if (notFound) {
        return <Navigate to="/error" />;
    }

    if (isLoading) {
        return <ProfileSkeleton />;
    }

    const navigateToEditProfile = () => {
        axios.post('http://localhost:4567/tokenvalidation', {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        }).then(() => {
            navigate(`/profile/${localStorage.getItem("username")}/edit`);
        }).catch(e => {
            console.error('Error:', e);
            showAlert('Failed to validate token. Please try again.');
        });
    };

    const handleAddFriend = async () => {
        const isLoggedIn = await validateLogin();
        if (!isLoggedIn) {
            showAlert("You need to be logged in to perform this action.");
            return;
        }

        axios.put(`http://localhost:4567/user/friends/send/${localStorage.getItem("currentProfileId")}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                console.log("Friend request sent:", response);
                setIsPending(true);
            })
            .catch(error => {
                console.error("Error adding friend:", error);
                showAlert('Failed to send friend request. Please try again.');
            });
    };

    const handleDeleteFriend = async () => {
        const isLoggedIn = await validateLogin();
        if (!isLoggedIn) {
            showAlert("You need to be logged in to perform this action.");
            return;
        }

        axios.put('http://localhost:4567/user/friends/remove/' + localStorage.getItem("currentProfileId"), {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() => {
                setIsFriend(false);
                setIsPending(false);
            })
            .catch(error => {
                console.error("Error removing friend:", error);
                showAlert('Failed to remove friend. Please try again.');
            });
    };

    return (
        <div>
            <Header />
            <div className='relative'>
                <div className='flex flex-col md:flex-row'>
                    <div className='flex-grow'>
                        {/* Banner */}
                        <div className='bg-white relative'>
                            <img src={bannerImage} className="w-full h-[250px] object-cover border-2 border-black" alt="Banner"/>
                            {/* Buttons */}
                            {loggedInUsername === username ? (
                                // Edit Profile button for the user's own profile
                                <button onClick={navigateToEditProfile} className="absolute top-4 right-4 text-white py-2 px-4 rounded bg-black">
                                    Edit Profile
                                </button>
                            ) : (
                                // Add/Remove Friend and Follow/Unfollow buttons for other profiles
                                <div className="absolute top-4 right-4 flex space-x-2">
                                    {isFriend ? (
                                        <button onClick={handleDeleteFriend} className="bg-red-600 text-white py-2 px-4 rounded">
                                            Remove Friend
                                        </button>
                                    ) : (
                                        isPending ? (
                                            <button className="bg-gray-600 text-white py-2 px-4 rounded">
                                                Request Sent
                                            </button>
                                        ) : (
                                            <button onClick={handleAddFriend} className="bg-green-600 text-white py-2 px-4 rounded">
                                                Add Friend
                                            </button>
                                        )
                                    )}

                                    {localStorage.getItem('currentProfileRol') === 'DEVELOPER' && (
                                        <button onClick={handleFollow} className="bg-yellow-500 text-white py-2 px-4 rounded">
                                            {isFollowing ? 'Unfollow' : 'Follow'}
                                        </button>
                                    )}

                                    {/* Admin Action Button (only visible to admins) */}
                                    {localStorage.getItem('rol') === 'ADMIN' && (
                                        <button onClick={isBanned ? handleUnbanUser : handleAdminAction} className="bg-purple-600 text-white py-2 px-4 rounded">
                                            {isBanned ? 'Unban' : 'Ban'}
                                        </button>
                                    )}

                                </div>

                            )}
                            <Alert alert={alert} setAlert={setAlert} />
                            {/* Profile Information */}
                            <div className="flex w-4/5 md:w-3/4 lg:w-1/2 h-auto items-center mx-auto md:mx-16 z-40 -mt-32 rounded-lg p-4">
                                <img src={profilePicture} className="h-52 w-52 md:h-56 md:w-56 bg-gray-400 object-cover rounded-full border-2 border-black" alt="User Profile"/>
                                <div className="ml-4 pt-20">
                                    <div className="flex items-center justify-between pt-10 px-10">
                                        <h1 className="font-bold text-2xl md:text-3xl">{usernameResponse}</h1>
                                        {loggedInUsername !== username && isDonationsSetup && (
                                            <div style={{ position: 'absolute', right: '20px' }}>
                                                <PayPopup username={username} />
                                            </div>
                                        )}
                                    </div>
                                    <h2 className="font-semibold text-lg md:text-xl pt-2 pb-1 pl-14">About me</h2>
                                    <p className="font-normal pl-14">{description}</p>
                                </div>
                            </div>
                        </div>
                        {/* Shelves */}
                        <div className="bg-white mt-8">
                            <h1 className="pl-4 md:pl-16 font-bold text-4xl pt-5">All Shelves</h1>
                            <Shelves username={username} />
                        </div>
                    </div>
                </div>
            </div>
            {showPopup && <StatusPopup status={status} onClose={handleClosePopup} preferenceId={preferenceId} username={username} />}
        </div>
    );
}

export default Profile;
