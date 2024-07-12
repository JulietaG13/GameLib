import React, {useState, useRef} from 'react';
import { initMercadoPago, Wallet } from '@mercadopago/sdk-react';
import PropTypes from 'prop-types';
import './PayPopup.css';
import axios from "axios";

const PayPopup = ({ username }) => {
    const [isVisible, setIsVisible] = useState(false);
    const [inputValue, setInputValue] = useState('');
    const [showWallet, setShowWallet] = useState(false);
    const inputRef = useRef(null);
    const [preferenceID, setPreferenceID] = useState('');

    const togglePopup = () => {
        setIsVisible(!isVisible);
        setInputValue('');
        setShowWallet(false);
    };

    const handleInputChange = (e) => {
        setInputValue(e.target.value);
        if (showWallet) {
            setShowWallet(false);
        }
    };

    const handleInputBlur = async () => {
        if (inputValue !== '') {
            try {
                const response = await axios.post('http://localhost:4567/pay/pref/create/' + username, {
                    amount: inputValue,
                    back_url: 'http://localhost:3000/profile/' + username,
                });

                const data = response.data;
                setPreferenceID(data.preference_id)
                initMercadoPago(data.public_key, { locale: 'es-AR' })   // public key

                setShowWallet(true);
            } catch (error) {
                console.error('Error:', error);
            }
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            inputRef.current.blur();
        }
        if (e.key === '-' || e.key === '.') {
            e.preventDefault();
        }
    };

    const mpCustomization = {
        texts: {
            action: 'pay',
            valueProp: 'security_details',
        },
        visual: {
            buttonBackground: 'black',
            borderRadius: '6px',
            valuePropColor: 'grey'
        },
        checkout: {
            theme: {
                elementsColor: '#4287F5',
                headerColor: '#4287F5'
            },
        }
    }
    //https://www.mercadopago.com.ar/developers/es/docs/checkout-pro/checkout-customization/user-interface/change-button-texts#editor_2

    return (
        <div>
            <button className="donate-button" onClick={togglePopup}>Support the Developer</button>

            {isVisible && (
                <div className="popup-overlay" onClick={togglePopup}>
                    <div className="popup-content" onClick={e => e.stopPropagation()}>
                        <h2>Donate</h2>

                        <input
                            ref={inputRef}
                            type="number"
                            value={inputValue}
                            onChange={handleInputChange}
                            onBlur={handleInputBlur}
                            onKeyDown={handleKeyDown}
                            placeholder="Ingrese el monto"
                            className="number-input"
                        />

                        {showWallet && (
                            <Wallet
                                initialization={{ preferenceId: preferenceID }}
                                customization={mpCustomization}
                            />
                        )}

                        <div className="container-popup">
                            <button className="cancel-button" onClick={togglePopup}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

PayPopup.propTypes = {
    username: PropTypes.string.isRequired,
};

export default PayPopup;
