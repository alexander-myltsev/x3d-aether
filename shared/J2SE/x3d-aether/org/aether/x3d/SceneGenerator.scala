package org.aether.x3d;

class SceneGenerator(time:Long) {
	def getScene(time:Long) =
		"""
          <Scene>
            <Viewpoint position='0 0 10' />
             <Shape>
                <Appearance>
                    <Material diffuseColor='0.603 0.894 0.909' />	
                </Appearance>
                <Box DEF='box'/>
             </Shape>
          </Scene> 
		"""
}