import React, {useEffect} from 'react';
import './StatusPopup.css';
import axios from "axios";

const StatusPopup = ({ status, onClose, preferenceId, username }) => {

    useEffect(() => {
        const fetchData = async () => {
            if (status === 'approved') {
                try {
                    await axios.post('http://localhost:4567/pay/pref/save/' + username, {
                        preference_id: preferenceId,
                    });
                } catch (error) {
                    console.error('Error:', error);
                }
            }
        };

        fetchData();
    }, [status, preferenceId]);

    let message = '';

    switch (status) {
        case 'approved':
            message = 'Thank you for your support!';
            break;
        case 'pending':
            message = 'Payment is pending';
            break;
        case 'rejected':
        case 'null':
        default:
            message = 'Something went wrong :(';
            break;
    }

    return (
        <div className="popup-overlay" onClick={onClose}>
            <div className="popup-content" onClick={e => e.stopPropagation()}>
                <h2 style={{ textAlign: 'center', fontWeight: 'bold' }}>{message}</h2>
                <div className="container-popup">
                    <button className="close-button" onClick={onClose}>OK</button>
                </div>
            </div>
        </div>
    );
};

export default StatusPopup;
