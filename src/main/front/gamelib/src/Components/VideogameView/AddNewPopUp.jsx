import React, {useState} from "react";
import plus_icon from "../Assets/plus-icon.png";
import axios from "axios";
import './AddNewPopUp.css';

function AddNewPopUp(props) {
    const [showForm, setShowForm] = useState(false);

    const [newToPublish, setNewToPublish] = useState({
        title: '',
        description: ''
    });

    const handleShow = () => {
        setShowForm(!showForm);
    }

    const handleNewSubmit = (e) => {
        // e.preventDefault();
        console.log(newToPublish);
        console.log(props.videogameID);
        axios.post(`http://localhost:4567/news/add/game/${props.videogameID}`, newToPublish, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(r =>
                setNewToPublish({
                    title: '',
                    description: ''
                })
            )
            .catch(e => console.error(e));
    }

    return (
        <div>
            <button id={'plus'} onClick={handleShow} ><img alt={'Add new'} title={'Add a new'} src={plus_icon} /></button>
            {showForm ?
                <form className={'addNewDiv'} onSubmit={handleNewSubmit} style={{ marginLeft: '-5em', borderRadius: '8px' }}>
                    <div className="new-popup-container">
                        <div className={'newTitleDiv'}>
                            <div style={{ marginLeft: '0' }}>
                                <label>Title</label>
                            </div>
                            <input type="text"
                                   name="title"
                                   value={newToPublish.title}
                                   onChange={
                                       e =>
                                           setNewToPublish({...newToPublish, title: e.target.value})
                                   } required
                                   style={{borderRadius: '5px', marginLeft: '0'}}
                            />
                        </div>
                        <div className={'newDescDiv'}>
                            <div style={{ marginLeft: '0' }}>
                                <label>Description</label>
                            </div>
                            <textarea name="description"
                                      value={newToPublish.description}
                                      onChange={
                                          e =>
                                              setNewToPublish({...newToPublish, description: e.target.value})
                                      } required
                                      style={{borderRadius: '5px', marginLeft: '0', width: '85%'}}
                            />
                        </div>
                    </div>
                    <button type="submit" style={{whiteSpace: 'pre'}}>
                        {'  Publish  '}
                    </button>
                </form>
                :
                null
            }
        </div>
    );
}

export default AddNewPopUp;
