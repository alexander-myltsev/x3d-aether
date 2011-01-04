//	---------------------------------------------------------------------------
//	jWebSocket Filesyste, Client PlugIn (uses jWebSocket Client and Server)
//	(C) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH, Herzogenrath
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------


//	---------------------------------------------------------------------------
//  jWebSocket Filesystem Client Plug-In
//	---------------------------------------------------------------------------

jws.FileSystemPlugIn = {

	// namespace for filesystem plugin
	// if namespace is changed update server plug-in accordingly!
	NS: jws.NS_BASE + ".plugins.filesystem",

	processToken: function( aToken ) {
		// check if namespace matches
		if( aToken.ns == jws.FileSystemPlugIn.NS ) {
			// here you can handle incomimng tokens from the server
			// directy in the plug-in if desired.
			if( "load" == aToken.reqType ) {
				if( aToken.code == 0 ) {
					aToken.data = Base64.decode( aToken.data );
					if( this.OnFileLoaded ) {
						this.OnFileLoaded( aToken );
					}
				} else {
					if( this.OnFileError ) {
						this.OnFileError( aToken );
					}
				}
			} else if( "event" == aToken.type ) {
				if( "filesaved" == aToken.name ) {
					if( this.OnFileSaved ) {
						this.OnFileSaved( aToken );
					}
				}
			}
		}
	},

	fileLoad: function( aFilename, aOptions ) {
		var lRes = this.createDefaultResult();
		if( this.isConnected() ) {
			var lToken = {
				ns: jws.FileSystemPlugIn.NS,
				type: "load",
				filename: aFilename
			};
			this.sendToken( lToken,	aOptions );
		} else {
			lRes.code = -1;
			lRes.localeKey = "jws.jsc.res.notConnected";
			lRes.msg = "Not connected.";
		}
		return lRes;
	},

	fileSave: function( aFilename, aData, aOptions ) {
		var lRes = this.createDefaultResult();
		if( this.isConnected() ) {
			var lToken = {
				ns: jws.FileSystemPlugIn.NS,
				type: "save",
				data: Base64.encode( aData ),
				filename: aFilename
			};
			this.sendToken( lToken,	aOptions );
		} else {
			lRes.code = -1;
			lRes.localeKey = "jws.jsc.res.notConnected";
			lRes.msg = "Not connected.";
		}
		return lRes;
	},

	setFileSystemCallbacks: function( aListeners ) {
		if( !aListeners ) {
			aListeners = {};
		}
		if( aListeners.OnFileLoaded !== undefined ) {
			this.OnFileLoaded = aListeners.OnFileLoaded;
		}
		if( aListeners.OnFileSaved !== undefined ) {
			this.OnFileSaved = aListeners.OnFileSaved;
		}
		if( aListeners.OnFileError !== undefined ) {
			this.OnFileError = aListeners.OnFileError;
		}
	}

}

// add the JWebSocket Shared Objects PlugIn into the TokenClient class
jws.oop.addPlugIn( jws.jWebSocketTokenClient, jws.FileSystemPlugIn );
