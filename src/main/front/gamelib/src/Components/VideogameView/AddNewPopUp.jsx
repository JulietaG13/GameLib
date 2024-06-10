import React, {useState} from "react";
import plus_icon from "../Assets/plus-icon.png";
import axios from "axios";

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
                <form className={'addNewDiv'} onSubmit={handleNewSubmit} >
                    <div className={'newTitleDiv'} >
                        <label>Title: </label>
                        <input type="text"
                               name="title"
                               value={newToPublish.title}
                               onChange={
                                   e =>
                                       setNewToPublish({...newToPublish, title: e.target.value})
                               } required/>
                    </div>
                    <div className={'newDescDiv'} >
                        <label>Description: </label>
                        <textarea name="description"
                                  value={newToPublish.description}
                                  onChange={
                                      e =>
                                          setNewToPublish({...newToPublish, description: e.target.value})
                                  } required/>
                    </div>
                    <button type="submit">Add</button>
                </form>
                :
                null
            }
        </div>
    );
}

export default AddNewPopUp;
