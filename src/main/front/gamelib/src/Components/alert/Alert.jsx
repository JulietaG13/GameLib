import React from 'react';
import './Alert.css';

const Alert = ({ alert, setAlert }) => {
    if (!alert.visible) return null;

    return (
        <div style={{ position: 'absolute', top: alert.position.top, left: alert.position.left }}>
            <div className="alert">
                <span className="closebtn" onClick={() => setAlert({ ...alert, visible: false })}>&times;</span>
                {alert.message}
            </div>
        </div>
    );
};

export default Alert;
