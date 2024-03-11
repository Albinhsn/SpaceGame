#include "sdl.h"
#include "common.h"

PFNGLCREATESHADERPROC             glCreateShader             = NULL;
PFNGLCOMPILESHADERPROC            glCompileShader            = NULL;
PFNGLGETSHADERIVPROC              glGetShaderiv              = NULL;
PFNGLGETSHADERINFOLOGPROC         glGetShaderInfoLog         = NULL;
PFNGLCREATEPROGRAMPROC            glCreateProgram            = NULL;
PFNGLBINDATTRIBLOCATIONPROC       glBindAttribLocation       = NULL;
PFNGLLINKPROGRAMPROC              glLinkProgram              = NULL;
PFNGLSHADERSOURCEPROC             glShaderSource             = NULL;
PFNGLGETPROGRAMIVPROC             glGetProgramiv             = NULL;
PFNGLGETPROGRAMINFOLOGPROC        glGetProgramInfoLog        = NULL;
PFNGLDETACHSHADERPROC             glDetachShader             = NULL;
PFNGLDELETESHADERPROC             glDeleteShader             = NULL;
PFNGLDELETEPROGRAMPROC            glDeleteProgram            = NULL;
PFNGLUSEPROGRAMPROC               glUseProgram               = NULL;
PFNGLATTACHSHADERPROC             glAttachShader             = NULL;
PFNGLGETUNIFORMLOCATIONPROC       glGetUniformLocation       = NULL;
PFNGLUNIFORMMATRIX4FVPROC         glUniformMatrix4fv         = NULL;
PFNGLUNIFORMMATRIX3FVPROC         glUniformMatrix3fv         = NULL;
PFNGLGENVERTEXARRAYSPROC          glGenVertexArrays          = NULL;
PFNGLGENBUFFERSPROC               glGenBuffers               = NULL;
PFNGLBINDBUFFERPROC               glBindBuffer               = NULL;
PFNGLBUFFERDATAPROC               glBufferData               = NULL;
PFNGLENABLEVERTEXATTRIBARRAYPROC  glEnableVertexAttribArray  = NULL;
PFNGLVERTEXATTRIBPOINTERPROC      glVertexAttribPointer      = NULL;
PFNGLDISABLEVERTEXATTRIBARRAYPROC glDisableVertexAttribArray = NULL;
PFNGLDELETEBUFFERSPROC            glDeleteBuffers            = NULL;
PFNGLDELETEVERTEXARRAYSPROC       glDeleteVertexArrays       = NULL;
PFNGLUNIFORM1IPROC                glUniform1i                = NULL;
PFNGLGENERATEMIPMAPPROC           glGenerateMipmap           = NULL;
PFNGLUNIFORM2FVPROC               glUniform2fv               = NULL;
PFNGLUNIFORM3FVPROC               glUniform3fv               = NULL;
PFNGLUNIFORM4FVPROC               glUniform4fv               = NULL;
PFNGLMAPNAMEDBUFFERPROC           glMapNamedBuffer           = NULL;
PFNGLMAPBUFFERPROC                glMapBuffer                = NULL;
PFNGLUNMAPBUFFERPROC              glUnmapBuffer              = NULL;
PFNGLUNIFORM1FPROC                glUniform1f                = NULL;
PFNGLGENFRAMEBUFFERSPROC          glGenFramebuffers          = NULL;
PFNGLDELETEFRAMEBUFFERSPROC       glDeleteFramebuffers       = NULL;
PFNGLBINDFRAMEBUFFERPROC          glBindFramebuffer          = NULL;
PFNGLFRAMEBUFFERTEXTURE2DPROC     glFramebufferTexture2D     = NULL;
PFNGLGENRENDERBUFFERSPROC         glGenRenderbuffers         = NULL;
PFNGLBINDRENDERBUFFERPROC         glBindRenderbuffer         = NULL;
PFNGLRENDERBUFFERSTORAGEPROC      glRenderbufferStorage      = NULL;
PFNGLFRAMEBUFFERRENDERBUFFERPROC  glFramebufferRenderbuffer  = NULL;
PFNGLDRAWBUFFERSARBPROC           glDrawBuffers              = NULL;
PFNGLDELETERENDERBUFFERSPROC      glDeleteRenderbuffers      = NULL;
PFNGLBLENDFUNCSEPARATEPROC        glBlendFuncSeparate        = NULL;
PFNGLBINDVERTEXARRAYPROC          glBindVertexArray          = NULL;

void                              loadExtensions()
{
  glCreateShader             = (PFNGLCREATESHADERPROC)SDL_GL_GetProcAddress("glCreateShader");
  glCompileShader            = (PFNGLCOMPILESHADERPROC)SDL_GL_GetProcAddress("glCompileShader");
  glGetShaderiv              = (PFNGLGETSHADERIVPROC)SDL_GL_GetProcAddress("glGetShaderiv");
  glGetShaderInfoLog         = (PFNGLGETSHADERINFOLOGPROC)SDL_GL_GetProcAddress("glGetShaderInfoLog");
  glCreateProgram            = (PFNGLCREATEPROGRAMPROC)SDL_GL_GetProcAddress("glCreateProgram");
  glBindAttribLocation       = (PFNGLBINDATTRIBLOCATIONPROC)SDL_GL_GetProcAddress("glBindAttribLocation");
  glLinkProgram              = (PFNGLLINKPROGRAMPROC)SDL_GL_GetProcAddress("glLinkProgram");
  glShaderSource             = (PFNGLSHADERSOURCEPROC)SDL_GL_GetProcAddress("glShaderSource");
  glLinkProgram              = (PFNGLLINKPROGRAMPROC)SDL_GL_GetProcAddress("glLinkProgram");
  glGetProgramiv             = (PFNGLGETPROGRAMIVPROC)SDL_GL_GetProcAddress("glGetProgramiv");
  glGetProgramInfoLog        = (PFNGLGETPROGRAMINFOLOGPROC)SDL_GL_GetProcAddress("glGetProgramInfoLog");
  glDetachShader             = (PFNGLDETACHSHADERPROC)SDL_GL_GetProcAddress("glDetachShader");
  glDeleteShader             = (PFNGLDELETESHADERPROC)SDL_GL_GetProcAddress("glDeleteShader");
  glDeleteProgram            = (PFNGLDELETEPROGRAMPROC)SDL_GL_GetProcAddress("glDeleteProgram");
  glUseProgram               = (PFNGLUSEPROGRAMPROC)SDL_GL_GetProcAddress("glUseProgram");
  glAttachShader             = (PFNGLATTACHSHADERPROC)SDL_GL_GetProcAddress("glAttachShader");
  glGetUniformLocation       = (PFNGLGETUNIFORMLOCATIONPROC)SDL_GL_GetProcAddress("glGetUniformLocation");
  glUniformMatrix4fv         = (PFNGLUNIFORMMATRIX4FVPROC)SDL_GL_GetProcAddress("glUniformMatrix4fv");
  glUniformMatrix3fv         = (PFNGLUNIFORMMATRIX3FVPROC)SDL_GL_GetProcAddress("glUniformMatrix3fv");
  glGenVertexArrays          = (PFNGLGENVERTEXARRAYSPROC)SDL_GL_GetProcAddress("glGenVertexArrays");
  glBindVertexArray          = (PFNGLBINDVERTEXARRAYPROC)SDL_GL_GetProcAddress("glBindVertexArray");
  glGenBuffers               = (PFNGLGENBUFFERSPROC)SDL_GL_GetProcAddress("glGenBuffers");
  glBindBuffer               = (PFNGLBINDBUFFERPROC)SDL_GL_GetProcAddress("glBindBuffer");
  glBufferData               = (PFNGLBUFFERDATAPROC)SDL_GL_GetProcAddress("glBufferData");
  glEnableVertexAttribArray  = (PFNGLENABLEVERTEXATTRIBARRAYPROC)SDL_GL_GetProcAddress("glEnableVertexAttribArray");
  glVertexAttribPointer      = (PFNGLVERTEXATTRIBPOINTERPROC)SDL_GL_GetProcAddress("glVertexAttribPointer");
  glDisableVertexAttribArray = (PFNGLDISABLEVERTEXATTRIBARRAYPROC)SDL_GL_GetProcAddress("glDisableVertexAttribArray");
  glDeleteBuffers            = (PFNGLDELETEBUFFERSPROC)SDL_GL_GetProcAddress("glDeleteBuffers");
  glDeleteVertexArrays       = (PFNGLDELETEVERTEXARRAYSPROC)SDL_GL_GetProcAddress("glDeleteVertexArrays");
  glUniform1i                = (PFNGLUNIFORM1IPROC)SDL_GL_GetProcAddress("glUniform1i");
  glGenerateMipmap           = (PFNGLGENERATEMIPMAPPROC)SDL_GL_GetProcAddress("glGenerateMipmap");
  glUniform2fv               = (PFNGLUNIFORM2FVPROC)SDL_GL_GetProcAddress("glUniform2fv");
  glUniform3fv               = (PFNGLUNIFORM3FVPROC)SDL_GL_GetProcAddress("glUniform3fv");
  glUniform4fv               = (PFNGLUNIFORM4FVPROC)SDL_GL_GetProcAddress("glUniform4fv");
  glMapNamedBuffer           = (PFNGLMAPNAMEDBUFFERPROC)SDL_GL_GetProcAddress("glMapNamedBuffer");
  glMapBuffer                = (PFNGLMAPBUFFERPROC)SDL_GL_GetProcAddress("glMapBuffer");
  glUnmapBuffer              = (PFNGLUNMAPBUFFERPROC)SDL_GL_GetProcAddress("glUnmapBuffer");
  glUniform1f                = (PFNGLUNIFORM1FPROC)SDL_GL_GetProcAddress("glUniform1f");
  glGenFramebuffers          = (PFNGLGENFRAMEBUFFERSPROC)SDL_GL_GetProcAddress("glGenFramebuffers");
  glDeleteFramebuffers       = (PFNGLDELETEFRAMEBUFFERSPROC)SDL_GL_GetProcAddress("glDeleteFramebuffers");
  glBindFramebuffer          = (PFNGLBINDFRAMEBUFFERPROC)SDL_GL_GetProcAddress("glBindFramebuffer");
  glFramebufferTexture2D     = (PFNGLFRAMEBUFFERTEXTURE2DPROC)SDL_GL_GetProcAddress("glFramebufferTexture2D");
  glGenRenderbuffers         = (PFNGLGENRENDERBUFFERSPROC)SDL_GL_GetProcAddress("glGenRenderbuffers");
  glBindRenderbuffer         = (PFNGLBINDRENDERBUFFERPROC)SDL_GL_GetProcAddress("glBindRenderbuffer");
  glRenderbufferStorage      = (PFNGLRENDERBUFFERSTORAGEPROC)SDL_GL_GetProcAddress("glRenderbufferStorage");
  glFramebufferRenderbuffer  = (PFNGLFRAMEBUFFERRENDERBUFFERPROC)SDL_GL_GetProcAddress("glFramebufferRenderbuffer");
  glDrawBuffers              = (PFNGLDRAWBUFFERSARBPROC)SDL_GL_GetProcAddress("glDrawBuffers");
  glDeleteRenderbuffers      = (PFNGLDELETERENDERBUFFERSPROC)SDL_GL_GetProcAddress("glDeleteRenderbuffers");
  glBlendFuncSeparate        = (PFNGLBLENDFUNCSEPARATEPROC)SDL_GL_GetProcAddress("glBlendFuncSeparate");
}

GLuint sta_glCreateShader(GLenum type)
{
  return glCreateShader(type);
}
void sta_glCompileShader(GLuint shader)
{
  glCompileShader(shader);
}
void sta_glGetShaderiv(GLuint shader, GLenum pname, GLint* params)
{
  glGetShaderiv(shader, pname, params);
}
void sta_glGetShaderInfoLog(GLuint shader, GLsizei bufSize, GLsizei* length, char* infoLog)
{
  glGetShaderInfoLog(shader, bufSize, length, infoLog);
}
GLuint sta_glCreateProgram()
{
  return glCreateProgram();
}
void sta_glAttachShader(GLuint program, GLuint shader)
{
  glAttachShader(program, shader);
}
void sta_glBindAttribLocation(GLuint program, GLuint index, const char* name)
{
  glBindAttribLocation(program, index, name);
}
void sta_glLinkProgram(GLuint program)
{
  glLinkProgram(program);
}
void sta_glGetProgramiv(GLuint program, GLenum pname, GLint* params)
{
  glGetProgramiv(program, pname, params);
}
void sta_glGetProgramInfoLog(GLuint program, GLsizei bufSize, GLsizei* length, char* infoLog)
{
  glGetProgramInfoLog(program, bufSize, length, infoLog);
}
void sta_glDetachShader(GLuint program, GLuint shader)
{
  glDetachShader(program, shader);
}
void sta_glDeleteProgram(GLuint program)
{
  glDeleteProgram(program);
}
void sta_glUseProgram(GLuint program)
{
  glUseProgram(program);
}
GLint sta_glGetUniformLocation(GLuint program, const char* name)
{
  return glGetUniformLocation(program, name);
}
void sta_glUniformMatrix4fv(GLint location, GLsizei count, GLboolean transpose, const GLfloat* value)
{
  glUniformMatrix4fv(location, count, transpose, value);
}

void sta_glUniformMatrix3fv(GLint location, GLsizei count, GLboolean transpose, const GLfloat* value)
{
  glUniformMatrix3fv(location, count, transpose, value);
}
void sta_glGenVertexArrays(GLsizei n, GLuint* arrays)
{
  glGenVertexArrays(n, arrays);
}
void sta_glBindVertexArray(GLuint array)
{
  glBindVertexArray(array);
}
void sta_glGenBuffers(GLsizei n, GLuint* buffers)
{
  glGenBuffers(n, buffers);
}
void sta_glBindBuffer(GLenum target, GLuint buffer)
{
  glBindBuffer(target, buffer);
}
void sta_glBufferData(GLenum target, ptrdiff_t size, const GLvoid* data, GLenum usage)
{
  glBufferData(target, size, data, usage);
}
void sta_glEnableVertexAttribArray(GLuint index)
{
  glEnableVertexAttribArray(index);
}
void sta_glVertexAttribPointer(GLuint index, GLint size, GLenum type, GLboolean normalized, GLsizei stride, const GLvoid* pointer)
{
  glVertexAttribPointer(index, size, type, normalized, stride, pointer);
}
void sta_glDisableVertexAttribArray(GLuint index)
{
  glDisableVertexAttribArray(index);
}
void sta_glDeleteBuffers(GLsizei n, const GLuint* buffers)
{
  glDeleteBuffers(n, buffers);
}
void sta_glDeleteVertexArrays(GLsizei n, const GLuint* arrays)
{
  glDeleteVertexArrays(n, arrays);
}
void sta_glUniform1i(GLint location, GLint v0)
{
  glUniform1i(location, v0);
}
void sta_glGenerateMipmap(GLenum target)
{
  glGenerateMipmap(target);
}
void sta_glUniform2fv(GLint location, GLsizei count, const GLfloat* value)
{
  glUniform2fv(location, count, value);
}
void sta_glUniform3fv(GLint location, GLsizei count, const GLfloat* value)
{
  glUniform3fv(location, count, value);
}
void sta_glUniform4fv(GLint location, GLsizei count, const GLfloat* value)
{
  glUniform4fv(location, count, value);
}
void* sta_glMapBuffer(GLenum target, GLenum access)
{
  return glMapBuffer(target, access);
}
GLboolean sta_glUnmapBuffer(GLenum target)
{
  return glUnmapBuffer(target);
}
void sta_glUniform1f(GLint location, GLfloat v0)
{
  glUniform1f(location, v0);
}
void sta_glGenFramebuffers(GLsizei n, GLuint* framebuffers)
{
  glGenFramebuffers(n, framebuffers);
}
void sta_glDeleteFramebuffers(GLsizei n, const GLuint* framebuffers)
{
  glDeleteFramebuffers(n, framebuffers);
}
void sta_glBindFramebuffer(GLenum target, GLuint framebuffer)
{
  glBindFramebuffer(target, framebuffer);
}
void sta_glFramebufferTexture2D(GLenum target, GLenum attachment, GLenum textarget, GLuint texture, GLint level)
{
  glFramebufferTexture2D(target, attachment, textarget, texture, level);
}
void sta_glGenRenderbuffers(GLsizei n, GLuint* renderbuffers)
{
  glGenRenderbuffers(n, renderbuffers);
}
void sta_glBindRenderbuffer(GLenum target, GLuint renderbuffer)
{
  glBindRenderbuffer(target, renderbuffer);
}
void sta_glRenderbufferStorage(GLenum target, GLenum internalformat, GLsizei width, GLsizei height)
{
  glRenderbufferStorage(target, internalformat, width, height);
}
void sta_glFramebufferRenderbuffer(GLenum target, GLenum attachment, GLenum renderbuffertarget, GLuint renderbuffer)
{
  glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
}
void sta_glDeleteRenderbuffers(GLsizei n, const GLuint* renderbuffers)
{
  glDeleteRenderbuffers(n, renderbuffers);
}
void sta_glBlendFuncSeparate(GLenum sfactorRGB, GLenum dfactorRGB, GLenum sfactorAlpha, GLenum dfactorAlpha)
{
  glBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
}
void sta_glShaderSource(GLuint shader, GLsizei count, const GLchar* const* string, const GLint* length)
{
  glShaderSource(shader, count, string, length);
}
void sta_glMapNamedBuffer(GLuint buffer, GLenum access)
{
  glMapNamedBuffer(buffer, access);
}
void sta_glDrawBuffers(GLint n, const GLenum* bufs)
{
  glDrawBuffers(n, bufs);
}

SDL_Window* initSDLWindow(SDL_GLContext* context, int screenWidth, int screenHeight)
{
  SDL_Window* window;

  SDL_Init(SDL_INIT_VIDEO);
  window   = SDL_CreateWindow("client", 0, 0, screenWidth, screenHeight, SDL_WINDOW_OPENGL);
  *context = SDL_GL_CreateContext(window);

  SDL_GL_SetAttribute(SDL_GL_DOUBLEBUFFER, 1);
  f32 vsyncStateVariable = getStateVariable("vsync");
  printf("Init vsync with %f\n", vsyncStateVariable);
  SDL_GL_SetSwapInterval((i32)vsyncStateVariable);

  loadExtensions();

  glEnable(GL_BLEND);
  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  glViewport(0, 0, screenWidth, screenHeight);

  return window;
}

void TurnZBufferOn()
{
  glEnable(GL_DEPTH_TEST);
}
void TurnZBufferOff()
{
  glDisable(GL_DEPTH_TEST);
}

void EnableAlphaBlending()
{
  glEnable(GL_BLEND);
  glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
}
void DisableAlphaBlending()
{
  glDisable(GL_BLEND);
}
