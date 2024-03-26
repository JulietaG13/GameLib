import React, { useEffect, useState } from 'react';
import {View, Text, ScrollView} from 'react-native';
import { listUsers } from '../Api';

const UsersList = () => {
    const [users, setUsers] = useState([]);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const usersData = await listUsers();
                setUsers(usersData);
            } catch (error) {
                console.log("Error fetching users:", error);
            }
        };

        fetchUsers();
    }, []);

    return (
        <ScrollView style={{ marginTop: 20 }}>
            <View style={{
                flexDirection: 'row',
                borderBottomWidth: 1,
                borderColor: '#ddd',
                paddingBottom: 10,
                marginBottom: 10
            }}>
                <Text style={{
                    fontWeight: 'bold',
                    flex: 1,
                    textAlign: 'center'
                }}>Username</Text>
                <Text style={{
                    fontWeight: 'bold',
                    flex: 1,
                    textAlign: 'center'
                }}>Email</Text>
            </View>
            {users.map((user) => (
                <View key={user.id} style={{
                    flexDirection: 'row',
                    paddingBottom: 10,
                    marginBottom: 10,
                    borderBottomWidth: 1,
                    borderColor: '#eee'
                }}>
                    <Text style={{
                        flex: 1,
                        textAlign: 'center'
                    }}>{user.username}</Text>
                    <Text style={{
                        flex: 1,
                        textAlign: 'center'
                    }}>{user.email}</Text>
                </View>
            ))}
        </ScrollView>
    );
};

export default UsersList;