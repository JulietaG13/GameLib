import {useEffect, useState} from "react";
import axios from "axios";
import './ShelfManager.css';
import ErrorView from "../ErrorView/ErrorView";

function ShelfManager({props}) {
    const [shelves, setShelves] = useState([]);
    const [refreshShelves, setRefreshShelves] = useState(false);
    const [waitingForShelves, setWaitingForShelves] = useState(true);
    const [shelfToUpload, setShelfToUpload] = useState({
        name: '',
        is_private: false
    });

    const [shelfErrorMessage, setShelfErrorMessage] = useState('');

    useEffect(() => {
        axios.get(`http://localhost:4567/shelf/get/user/${localStorage.getItem('username')}/10`, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                setShelves(response.data);
                setWaitingForShelves(false);
                setShelfErrorMessage('');
            })
            .catch(error => {
                console.error('Error:', error);
                setWaitingForShelves(false);
                setShelfErrorMessage(error.response.data);
            });
    }, [refreshShelves]);

    const handleAddToShelf = (shelfID, gameID) => {
        console.log(shelfID);
        console.log(gameID);
        axios.put(`http://localhost:4567/shelf/add/${shelfID}/${gameID}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() => {
                console.log('Added game to shelf ' + shelfID);
                setRefreshShelves(!refreshShelves);
                setShelfErrorMessage('');
            })
            .catch(error => {
                console.error('Error:', error);
                setShelfErrorMessage(error.response.data);
            });
    }

    const handleRemoveFromShelf = (shelfID, gameID) => {
        axios.put(`http://localhost:4567/shelf/remove/${shelfID}/${gameID}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() => {
                setRefreshShelves(!refreshShelves);
                setShelfErrorMessage('');
            })
            .catch(error => {
                console.error('Error:', error);
                setShelfErrorMessage(error.response.data);
            });
    }

    const handleShelfSubmit = (e) => {
        e.preventDefault();
        console.log(shelfToUpload);
        setWaitingForShelves(true);
        axios.post(`http://localhost:4567/shelf/add`, shelfToUpload, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then((response) => {
                console.log('created shelf');
                console.log(response.data);
                setRefreshShelves(!refreshShelves);
                setShelfToUpload({
                    name: '',
                    is_private: false
                })
                setShelfErrorMessage('');
            })
            .catch(error => {
                console.error('Error:', error);
                console.log(error.response.data);
                setWaitingForShelves(false);
                setShelfErrorMessage(error.response.data);
            });
    }

    return (
        <div className={'shelvesPopUp'} >
            <div style={{textAlign: 'center', marginBottom: '0.5em'}}>
                <h2><b>Shelves Manager</b></h2>
            </div>
            <div className={'shelvesList'}>
                {waitingForShelves ?
                    <div className={'shelf'}>
                        <p>Loading shelves...</p>
                    </div>
                    :
                    shelves.length === 0 ?
                        <div className={'shelf'}>
                            <p>Create your own shelves!</p>
                        </div>
                        :
                        shelves.map(shelf => (
                            <div key={shelf.id} className={'shelf'}>
                                <p>{shelf.name}</p>
                                {shelfIncludesGame(shelf, props) ?
                                    <button onClick={() => {handleRemoveFromShelf(shelf.id, props.id)}}
                                    >Remove from shelf</button>
                                    :
                                    <button
                                        onClick={() => {handleAddToShelf(shelf.id, props.id)}}
                                        style={{ whiteSpace: 'pre' }}
                                    >
                                        {'  Add to shelf  '}
                                    </button>
                                }
                            </div>
                        ))
                }
            </div>
            {waitingForShelves ?
                null
                :
                <form className={'shelfCreator'} onSubmit={handleShelfSubmit}>
                    <div className={'shelfAttributes'}>
                        <input type={'text'}
                               placeholder={'Shelf name'}
                               required
                               value={shelfToUpload.name}
                               onChange={(e) => {
                                   setShelfToUpload({
                                       ...shelfToUpload,
                                       name: e.target.value
                                   });
                               }}
                               style={{ margin: '0.1em', marginLeft: '0.2em', padding: '0.4em' }}
                        />
                        <label>
                            <input type={"checkbox"}
                                   checked={shelfToUpload.is_private}
                                   onClick={() => {
                                       setShelfToUpload({
                                           ...shelfToUpload,
                                           is_private: !shelfToUpload.is_private
                                       });
                                   }}/>
                            <div style={{whiteSpace: 'pre'}}>
                                {' Private '}
                            </div>
                        </label>
                    </div>
                    <button type={'submit'}>Create shelf</button>
                    {shelfErrorMessage !== '' ?
                        <ErrorView message={shelfErrorMessage}/>
                        :
                        null
                    }
                </form>
            }
        </div>
    );
}

function shelfIncludesGame(shelf, props) {
    return shelf.games.some(game => game.id === props.id);
}

export default ShelfManager;
