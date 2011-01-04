/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.plugins.sharedobjects;

import org.jwebsocket.token.Token;

/**
 *
 * @author aschulze
 */
public class BaseSharedObject implements ISharedObject {

	@Override
	public void init(String aUserId) {
	}

	@Override
	public String getClassName() {
		return null;
	}

	@Override
	public Token read(String aSubId) {
		return null;
	}

	@Override
	public void write(String aSubId, Token aToken) {
	}

	@Override
	public Token invoke(Token aToken) {
		return null;
	}

	@Override
	public void cleanup() {
	}

	@Override
	public void lock(String aSubId, String aUserId) {
	}

	@Override
	public void unlock(String aSubId, String aUserId) {
	}

	@Override
	public void grant(String aSubId, String aUserId, int aRight) {
	}

	@Override
	public void revoke(String aSubId, String aUserId, int aRight) {
	}

	@Override
	public void registerClient(String aUserId, String aClientId) {
	}

	@Override
	public void unregisterClient(String aUserId, String aClientId) {
	}
}
