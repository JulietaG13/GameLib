import React, {useEffect, useState} from 'react';
import axios from 'axios';
import './ManagePopup.css';
import ErrorMessage from '../Popup/ErrorMessage';
import { FaQuestionCircle } from 'react-icons/fa';

const ManagePopup = ({ visible, onConfirm, onCancel }) => {
    const [publicKey, setPublicKey] = useState('');
    const [accessToken, setAccessToken] = useState('');
    const [isDonationsSetup, setIsDonationsSetup] = useState(false);
    const [disableDonationsExpanded, setDisableDonationsExpanded] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const username = localStorage.getItem("username");

    useEffect( () => {
        handleIsDonationsSetup();
    })

    const handleIsDonationsSetup = async => {
        axios.get(`http://localhost:4567/pay/setup/is/${username}`)
            .then(response => {
                const isSetup = response.data.is_setup;
                setIsDonationsSetup(isSetup);
            })
            .catch(() => {
                setIsDonationsSetup(false);
            });
    }

    if (!visible) {
        if (publicKey !== '' || accessToken !== '' || disableDonationsExpanded === true) {
            setPublicKey('');
            setAccessToken('');
            setDisableDonationsExpanded(false);
            handleIsDonationsSetup();
        }
        return null;
    }

    const showError = (message) => {
        setErrorMessage(message);
    };

    const hideError = () => {
        setErrorMessage('');
    };

    const handleConfirm = async () => {
        try {
            await axios.post('http://localhost:4567/pay/setup/set/' + username, {
                "public_key": publicKey,
                "access_token": accessToken
            }, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token')
                }
            });
            onConfirm();
        } catch (error) {
            showError("Something went wrong. \nTry refreshing the page.");
            console.error('Error al confirmar:', error);
        }
    };

    const openHelpPage = () => {
        window.open('https://drive.google.com/file/d/1KufJdi1ie7jNE5iJG5xQs6KCzpZ4sTu9/view?usp=sharing', '_blank');
    };

    const toggleDisableDonations = () => {
        setDisableDonationsExpanded(!disableDonationsExpanded);
    };

    const confirmDisableDonations = async () => {
        try {
            const response = await axios.post('http://localhost:4567/pay/setup/disable/' + username, {}, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token')
                }
            });
            onCancel();
        } catch (error) {
            showError("Something went wrong. \nTry refreshing the page.");
            console.error('Error al deshabilitar donaciones:', error);
        }
    };

    return (
        <div className="manage-popup-overlay" onClick={onCancel}>
            <div className="manage-popup-content" onClick={e => e.stopPropagation()}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h2 style={{ fontWeight: 'bold' }}>Set Up Donations</h2>
                    <FaQuestionCircle
                        style={{ cursor: 'pointer' }}
                        onClick={openHelpPage}
                        size={24}
                    />
                </div>
                <div className="manage-input-container">
                    <div style={{ marginTop: '10px' }}>
                        <label htmlFor="publicKey">Mercado Pago Public Key</label>
                        <input
                            className={"manage-input"}
                            type="text"
                            id="publicKey"
                            name="publicKey"
                            value={publicKey}
                            onChange={(e) => setPublicKey(e.target.value)}
                        />
                    </div>
                    <div>
                        <label htmlFor="accessToken">Mercado Pago Access Token</label>
                        <input
                            className={"manage-input"}
                            type="text"
                            id="accessToken"
                            name="accessToken"
                            value={accessToken}
                            onChange={(e) => setAccessToken(e.target.value)}
                        />
                    </div>
                </div>
                <div className="manage-container-popup">
                    <button className="manage-confirm-button" onClick={handleConfirm}>Confirm</button>
                    <button className="manage-cancel-button" onClick={onCancel}>Cancel</button>
                </div>
                { isDonationsSetup && (
                        <div className="disable-donations-container" style={{ overflow: 'hidden', transition: 'height 1s ease' }}>
                            {disableDonationsExpanded ? (
                                <div>
                                    <div className="disable-donations-label" onClick={toggleDisableDonations}>
                                        <span>Are you sure you want to disable donations?</span>
                                    </div>
                                    <div className="disable-donations">
                                        <button className="manage-disable-button" onClick={confirmDisableDonations}>
                                            Disable donations
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                <div className="disable-donations-label" onClick={toggleDisableDonations}>
                                    <span>Disable donations?</span>
                                </div>
                            )}
                        </div>
                    )
                }
            </div>
            {errorMessage && (
                <ErrorMessage
                    message={errorMessage}
                    onClose={hideError}
                />
            )}
        </div>
    );
};

export default ManagePopup;
