import {useEffect, useState} from "react";
import axios from "axios";
import './ShelfManager.css';

function ShelfManager({props}) {
    // console.log(props);
    const [shelves, setShelves] = useState([]);
    const [refreshShelves, setRefreshShelves] = useState(false);

    const [shelfToUpload, setShelfToUpload] = useState({
        name: '',
        is_private: false
    });

    useEffect(() => {
        axios.get(`http://localhost:4567/shelf/get/user/${localStorage.getItem('username')}/10`, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                // console.log(response.data);
                setShelves(response.data);
            })
            .catch(error => {
                console.error('Error:', error);
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
            .then((response) => {
                console.log('added game to shelf');
                console.log(response.data);
                // setShelves(response.data);
                setRefreshShelves(!refreshShelves);
            })
            .catch(error => {
                console.error('Error:', error);
            });
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
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    useEffect(() => {
        console.log('shelves');
        console.log(shelves);
        // shelves.map(shelf => console.log(shelf.games));
        // shelves.map(shelf => shelf.games.map(game => console.log(game)));
    }, [shelves]);

    return (
        <div className={'shelvesPopUp'} >
            <h2>Shelves Manager</h2>
            <div className={'shelvesList'}>
                {shelves.map(shelf => (
                    <div key={shelf.id} className={'shelf'}>
                        {shelf.name}
                        {shelfIncludesGame(shelf, props) ?
                            <button onClick={() => {console.log('removed game from shelf')}}
                            >Remove from shelf</button>
                            :
                            <button onClick={() => {handleAddToShelf(shelf.id, props.id)}}
                            >Add to shelf</button>
                        }
                    </div>
                ))}
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
        </div>
    );
}

function shelfIncludesGame(shelf, props) {
    return shelf.games.some(game => game.id === props.id);
}

export default ShelfManager;
