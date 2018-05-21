import { NativeModules } from 'react-native';
import invariant from 'invariant';

const { RNGoogleAppauth } = NativeModules;

const checkScopes = scopes =>
  invariant(scopes && scopes.length, 'Scope error: please add at least one scope');
const checkClientId = clientId =>
  invariant(typeof clientId === 'string', 'Config error: clientId must be a string');
const checkRedirectUrl = redirectUrl =>
  invariant(typeof redirectUrl === 'string', 'Config error: redirectUrl must be a string');

export const signIn = async ({
  redirectUrl,
  clientId,
  scopes,
  additionalParameters,
}) => {
  checkScopes(scopes);
  checkClientId(clientId);
  checkRedirectUrl(redirectUrl);

  return await RNGoogleAppauth.signIn(
    redirectUrl,
    clientId,
    scopes,
    additionalParameters,
  );
};

export default RNGoogleAppauth;
