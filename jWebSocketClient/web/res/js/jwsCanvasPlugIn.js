//	---------------------------------------------------------------------------
//	jWebSocket Sample Client PlugIn (uses jWebSocket Client and Server)
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
//  jWebSocket Sample Client Plug-In
//	---------------------------------------------------------------------------

jws.CanvasPlugIn = {

	// namespace for shared objects plugin
	// if namespace is changed update server plug-in accordingly!
	NS: jws.NS_BASE + ".plugins.canvas",

	processToken: function( aToken ) {
		// check if namespace matches
		if( aToken.reqNS == jws.CanvasPlugIn.NS ) {
			// here you can handle incomimng tokens from the server
			// directy in the plug-in if desired.
			if( "clear" == aToken.reqType ) {
				this.doClear( aToken.id );
			} else if( "beginPath" == aToken.reqType ) {
				this.doBeginPath( aToken.id );
			} else if( "moveTo" == aToken.reqType ) {
				this.doMoveTo( aToken.id, aToken.x, aToken.y );
			} else if( "lineTo" == aToken.reqType ) {
				this.doLineTo( aToken.id, aToken.x, aToken.y );
			} else if( "closePath" == aToken.reqType ) {
				this.doClosePath( aToken.id );
			}
		}
	},

	fCanvas: {},

	canvasOpen: function( aId, aElementId ) {
		var lElem = jws.$( aElementId );
		this.fCanvas[ aId ] = {
			fDOMElem: lElem,
			ctx: lElem.getContext( "2d" )
		};
	},

	canvasClose: function( aId ) {
		this.fCanvas[ aId ] = null;
		delete this.fCanvas[ aId ];
	},

	doClear: function( aId ) {
		var lCanvas = this.fCanvas[ aId ];
		if( lCanvas != null ) {
			var lW = lCanvas.fDOMElem.getAttribute( "width" );
			var lH = lCanvas.fDOMElem.getAttribute( "height" );
			lCanvas.ctx.clearRect( 0, 0, lW, lH );
			return true;
		}
		return false;
	},

	canvasClear: function( aId ) {
		if( this.doClear( aId ) ) {
			var lToken = {
				reqNS: jws.CanvasPlugIn.NS,
				reqType: "clear",
				id: aId
			};
			this.broadcastToken(lToken);
		}
	},

	doBeginPath: function( aId ) {
		var lCanvas = this.fCanvas[ aId ];
		if( lCanvas != null ) {
			// console.log( "doBeginPath: " + aId);
			lCanvas.ctx.beginPath();
			return true;
		}
		return false;
	},

	canvasBeginPath: function( aId ) {
		if( this.doBeginPath( aId ) ) {
			var lToken = {
				reqNS: jws.CanvasPlugIn.NS,
				reqType: "beginPath",
				id: aId
			};
			this.broadcastToken(lToken);
		}
	},

	doMoveTo: function( aId, aX, aY ) {
		var lCanvas = this.fCanvas[ aId ];
		if( lCanvas != null ) {
			// console.log( "doMoveTo: " + aId + ", x:" + aX + ", y: " + aX );
			lCanvas.ctx.moveTo( aX, aY );
			return true;
		}
		return false;
	},

	canvasMoveTo: function( aId, aX, aY ) {
		if( this.doMoveTo( aId, aX, aY ) ) {
			var lToken = {
				reqNS: jws.CanvasPlugIn.NS,
				reqType: "moveTo",
				id: aId,
				x: aX,
				y: aY
			};
			this.broadcastToken(lToken);
		}
	},

	doLineTo: function( aId, aX, aY ) {
		var lCanvas = this.fCanvas[ aId ];
		if( lCanvas != null ) {
			// console.log( "doLineTo: " + aId + ", x:" + aX + ", y: " + aX );
			lCanvas.ctx.lineTo( aX, aY );
			lCanvas.ctx.stroke();
			return true;
		}
		return false;
	},

	canvasLineTo: function( aId, aX, aY ) {
		if( this.doLineTo( aId, aX, aY ) ) {
			var lToken = {
				reqNS: jws.CanvasPlugIn.NS,
				reqType: "lineTo",
				id: aId,
				x: aX,
				y: aY
			};
			this.broadcastToken(lToken);
		}
	},

	doClosePath: function( aId ) {
		var lCanvas = this.fCanvas[ aId ];
		if( lCanvas != null ) {
			// console.log( "doClosePath" );
			lCanvas.ctx.closePath();
			return true;
		}
		return false;
	},

	canvasClosePath: function( aId ) {
		if( this.doClosePath( aId ) ) {
			var lToken = {
				reqNS: jws.CanvasPlugIn.NS,
				reqType: "closePath",
				id: aId
			};
			this.broadcastToken(lToken);
		}
	}

}

// add the JWebSocket Shared Objects PlugIn into the TokenClient class
jws.oop.addPlugIn( jws.jWebSocketTokenClient, jws.CanvasPlugIn );
