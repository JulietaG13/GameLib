import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import UserList from "./users/UserList";

export default function App() {
  return (
    <View style={styles.container}>
      <UserList />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
