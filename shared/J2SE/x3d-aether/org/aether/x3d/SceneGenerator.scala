package org.aether.x3d;

object SceneGenerator {
	def getScene() = 
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