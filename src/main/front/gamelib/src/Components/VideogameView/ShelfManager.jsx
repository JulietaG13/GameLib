import {useEffect, useState} from "react";
import axios from "axios";
import './ShelfManager.css';
import ErrorView from "../ErrorView/ErrorView";

function ShelfManager({props}) {
    const [shelves, setShelves] = useState([]);
    const [refreshShelves, setRefreshShelves] = useState(false);
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
                setShelfErrorMessage('');
            })
            .catch(error => {
                console.error('Error:', error);
                setShelfErrorMessage(error.response.data.message);
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
                setRefreshShelves(!refreshShelves);
                setShelfErrorMessage('');
            })
            .catch(error => {
                console.error('Error:', error);
                setShelfErrorMessage(error.response.data.message);
            });
    }

    const handleRemoveFromShelf = (shelfID, gameID) => {
        console.log('Removing game' + gameID + ' from shelf ' + shelfID);
    }

    const handleShelfSubmit = (e) => {
        e.preventDefault();
        console.log(shelfToUpload);
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
                setShelfErrorMessage(error.response.data.message);
            });
    }

    return (
        <div className={'shelvesPopUp'} >
            <h2>Shelves Manager</h2>
            <div className={'shelvesList'}>
                {shelves.length === 0 ?
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
                                <button onClick={() => {handleAddToShelf(shelf.id, props.id)}}
                                >Add to shelf</button>
                            }
                        </div>
                    ))
                }
            </div>
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
                        <p>Private shelf</p>
                    </label>
                </div>
                <button type={'submit'}>Create shelf</button>
            </form>
            <ErrorView message={shelfErrorMessage}/>
        </div>
    );
}

function shelfIncludesGame(shelf, props) {
    return shelf.games.some(game => game.id === props.id);
}

export default ShelfManager;
