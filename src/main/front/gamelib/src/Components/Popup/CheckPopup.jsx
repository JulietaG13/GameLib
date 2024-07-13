import React from 'react';
import './CheckPopup.css';

const CheckPopup = ({ message, onConfirm, onCancel }) => {

    return (
        <div className="message-popup-overlay" onClick={onCancel}>
            <div className="message-popup-content" onClick={e => e.stopPropagation()}>
                <h2 style={{ textAlign: 'center' }}>{message}</h2>
                <div className="message-container-popup">
                    <button className="message-confirm-button" onClick={onConfirm}>Confirm</button>
                    <button className="message-cancel-button" onClick={onCancel}>Cancel</button>
                </div>
            </div>
        </div>
    );
};

export default CheckPopup;
