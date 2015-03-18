#version 120
/**
 * Simple pass-through vertex shader: 
 *     passes position on (after multiplying by current P*MV transform
 *     passes color on.
 * This is based on Angel's Demo Code
 */
uniform   mat4 pXmv;  // Composite of projection and modelview matrices
attribute vec4 vPosition;
attribute vec4 vColor;

attribute vec3 vNormal;
varying vec4 color;

varying  vec3 normal;
varying  vec3 fE;
varying  vec3 fL;

uniform mat4 ModelView;
uniform vec4 LightPosition;
uniform mat4 Projection;

void main()
{
    normal = vNormal;
    fE = vPosition.xyz;
    fL = (ModelView*LightPosition).xyz;

    if( LightPosition.w != 0.0 ) {
	fL = LightPosition.xyz - vPosition.xyz;
    }

    gl_Position = pXmv * vPosition;
    color = vColor;
}
