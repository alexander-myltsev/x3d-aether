package org.aether.x3d

import scala.xml._

class SceneGenerator(startTime:Long) {
	def getSimpleScene(time:Long) = {
		val posAttr = (((time - startTime) % 60 - 30) * 1.0f / 10.0f) + " 0 0"
		val scene = 
			<Scene>
				<Viewpoint position='0 0 10'></Viewpoint>				
				<Transform DEF='boxTrafo' translation={posAttr}>
					<Shape>
						<Appearance>
							<Material diffuseColor='0.603 0.894 0.909'></Material>
						</Appearance>
						<Box DEF='box'></Box>
					</Shape>
				</Transform>
			</Scene>
			
		val r = scene.child.mkString
		//val r = scene.mkString // Note: If replace whole scene of x3d node then rendering doesn't work.  
		r
	}
	
	def getScene(time:Long) = {
		val pos = ((time - startTime) % 200) * 1.0f / 100.0f
		val scene = 
			<scene>
				<Background skyColor='1 1 1' />
				
				<Transform DEF="coneTrafo" translation={"-4.5 0 " + (pos + 1)}>
					<Shape DEF="coneShape">
						<Appearance DEF="coneApp">
							<Material diffuseColor="0 1 0" specularColor=".5 .5 .5" />
						</Appearance>

						<Cone DEF="cone" />
					</Shape>
				</Transform>
				
				<Transform DEF="boxTrafo" translation={"-1.5 0 " + (2 * pos)}>
					<Shape DEF="boxShape">
						<Appearance DEF="boxApp"> 
							<Material diffuseColor="1 0 0" specularColor=".5 .5 .5" />
						</Appearance>
						<Box DEF="box" />

					</Shape>
				</Transform>
                
				<Transform DEF="sphereTrafo" translation={"1.5 0 " + (pos / 2)}>
					<Shape DEF="sphereShape">
						<Appearance DEF="sphereApp">
							<Material diffuseColor="0 0 1" specularColor=".5 .5 .5" />
						</Appearance>
						<Sphere DEF="sphere" />
					</Shape>

				</Transform>
				
				<Transform DEF="cylinderTrafo" translation={"4.5 0 " + (-pos)}>
					<Shape DEF="cylinderShape">
						<Appearance DEF="cylinderApp">
							<Material diffuseColor="1 1 0" specularColor=".5 .5 .5" />
						</Appearance>
						<Cylinder DEF="cylinder" radius="1.0" height="2.0" />
					</Shape>
				</Transform>

                
				<Viewpoint centerOfRotation="0 0 0" position="0 0 15" orientation="0 1 0 0" />
			</scene>
			
			val r = scene.child.mkString
			r
	}
}