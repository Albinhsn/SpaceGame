#version 400 

in vec2 inputPosition;
in vec2 inputTexCoord;

out vec2 texCoord;

void main(void)
{
  gl_Position = vec4(inputPosition.x, inputPosition.y, 0.0f, 1.0f);
  texCoord = inputTexCoord;
}
