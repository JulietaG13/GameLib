import React, { useEffect, useState } from 'react';
import './ErrorMessage.css'; // Estilo para el mensaje de error

const ErrorMessage = ({ message, onClose }) => {
    const [isVisible, setIsVisible] = useState(false);

    useEffect(() => {
        setIsVisible(true);

        const timer = setTimeout(() => {
            onClose();
            setIsVisible(false);
        }, 3000);

        return () => clearTimeout(timer);
    }, [message, onClose]);

    // render \n
    const renderMessage = () => {
        return message.split('\n').map((line, index) => (
            <p key={index}>{line}</p>
        ));
    };

    return (
        <div className={`error-message ${isVisible ? 'visible' : ''}`}>
            <div className="error-content">
                <span className="close-btn" onClick={onClose}>×</span>
                {renderMessage()}
            </div>
        </div>
    );
};

export default ErrorMessage;
