import React from 'react';

const AlertMessage = ({ message, onClose }) => {
    return (
        <div className="absolute bg-red-600 text-white p-2 rounded mt-2" style={{ zIndex: 1000, width: '300px', padding: '10px' }}>
            {message}
            <button onClick={onClose} className="ml-2 text-black">X</button>
        </div>
    );
};

export default AlertMessage;
