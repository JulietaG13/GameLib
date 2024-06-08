function ErrorView({message}) {
  return (
    <div className={'errorMessageDiv'}>
      <h1>{message}</h1>
    </div>
  );
}

export default ErrorView;
