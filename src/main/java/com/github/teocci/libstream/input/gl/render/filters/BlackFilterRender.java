package com.github.teocci.libstream.input.gl.render.filters;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.github.teocci.libstream.R;
import com.github.teocci.libstream.utils.gl.GlUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jan-14
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BlackFilterRender extends BaseFilterRender
{
    //rotation matrix
    private final float[] squareVertexDataFilter = {
            // X, Y, Z, U, V
            -1f, -1f, 0f, 0f, 0f, //bottom left
            1f, -1f, 0f, 1f, 0f, //bottom right
            -1f, 1f, 0f, 0f, 1f, //top left
            1f, 1f, 0f, 1f, 1f, //top right
    };

    private int program = -1;
    private int aPositionHandle = -1;
    private int aTextureHandle = -1;
    private int uMVPMatrixHandle = -1;
    private int uSTMatrixHandle = -1;
    private int uSamplerHandle = -1;

    public BlackFilterRender()
    {
        squareVertex = ByteBuffer.allocateDirect(squareVertexDataFilter.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        squareVertex.put(squareVertexDataFilter).position(0);
        Matrix.setIdentityM(MVPMatrix, 0);
        Matrix.setIdentityM(STMatrix, 0);
    }

    @Override
    protected void initGlFilter(Context context)
    {
        String vertexShader = GlUtil.getStringFromRaw(context, R.raw.simple_vertex);
        String fragmentShader = GlUtil.getStringFromRaw(context, R.raw.black_fragment);

        program = GlUtil.createProgram(vertexShader, fragmentShader);
        aPositionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        aTextureHandle = GLES20.glGetAttribLocation(program, "aTextureCoord");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        uSTMatrixHandle = GLES20.glGetUniformLocation(program, "uSTMatrix");
        uSamplerHandle = GLES20.glGetUniformLocation(program, "uSampler");
    }

    @Override
    protected void drawFilter()
    {
        GLES20.glUseProgram(program);

        squareVertex.position(SQUARE_VERTEX_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
                SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);
        GLES20.glEnableVertexAttribArray(aPositionHandle);

        squareVertex.position(SQUARE_VERTEX_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(aTextureHandle, 2, GLES20.GL_FLOAT, false,
                SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);
        GLES20.glEnableVertexAttribArray(aTextureHandle);

        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MVPMatrix, 0);
        GLES20.glUniformMatrix4fv(uSTMatrixHandle, 1, false, STMatrix, 0);

        GLES20.glUniform1i(uSamplerHandle, 4);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, previousTexId);
    }

    @Override
    public void release()
    {
        GLES20.glDeleteProgram(program);
    }
}
