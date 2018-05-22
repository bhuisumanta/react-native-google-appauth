import { NativeModules } from 'react-native';
import invariant from 'invariant';

const { RNGoogleAppauth } = NativeModules;

const checkScopes = scopes =>
  invariant(scopes && scopes.length, 'Scope error: please add at least one scope');
const checkClientId = clientId =>
  invariant(typeof clientId === 'string', 'Config error: clientId must be a string');
const checkRedirectUrl = redirectUrl =>
  invariant(typeof redirectUrl === 'string', 'Config error: redirectUrl must be a string');

export const configure = async ({
    redirectUrl,
    clientId,
    scopes,
    additionalParameters,
  }) => {
    checkScopes(scopes);
    checkClientId(clientId);
    checkRedirectUrl(redirectUrl);
  
    const res = await RNGoogleAppauth.configure(
      redirectUrl,
      clientId,
      scopes,
      additionalParameters,
    );

    if(res === "SIGNED_OUT"){
      return {status: false}
    }else {
      return JSON.parse(res);
    }
};

export const signIn = async () => {
  const res = await RNGoogleAppauth.signIn();
  try{
    return JSON.parse(res);
  } catch(err) {
    return res;
  }
};

export const signOut = async () => {
  const res = await RNGoogleAppauth.signOut();
  if(res === "SIGNED_OUT"){
    return {status: true};
  }else {
    return res;
  }
};

export default RNGoogleAppauth;
