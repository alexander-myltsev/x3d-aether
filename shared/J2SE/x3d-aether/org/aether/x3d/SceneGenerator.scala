package org.aether.x3d

import scala.xml._

class SceneGenerator(startTime:Long) {
	def getSimpleScene(time:Long) = {
		val posAttr = (((time - startTime) % 1000) * 1.0f / 100.0f) + " 0 0"
		val scene = 
			<scene>
				<viewpoint position="0 0 10"></viewpoint>				
				<transform DEF="boxTrafo" translation={posAttr}>
					<shape>
						<appearance>
							<material diffuseColor='0.603 0.894 0.909' ></material>	
						</appearance>
						<box DEF='box' ></box>
					</shape>
				</transform>
			</scene>
			
		val r = scene.child.mkString
		//val r = scene.mkString // Note: If replace whole scene of x3d node then rendering doesn't work.  
		r
	}
}