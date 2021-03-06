package rblocks.client.render;

import java.nio.FloatBuffer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import rblocks.api.IOrientable;
import rblocks.core.TileRotatableBlock;
import rblocks.util.Platform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RotateableBlockRender
{

	protected RotateableBlockRender() {
		setOriMap();
	}

	final int ORIENTATION_BITS = 7;
	final static int FLIP_H_BIT = 8;
	final static int FLIP_V_BIT = 16;

	protected abstract BlockRenderInfo getRendererInstance(Object block);

	final private byte OrientationMap[][][] = new byte[6][6][6];

	public int getOrientation(ForgeDirection in, ForgeDirection forward, ForgeDirection up)
	{
		if ( in == null || in.equals( ForgeDirection.UNKNOWN ) // 1
				|| forward == null || forward.equals( ForgeDirection.UNKNOWN ) // 2
				|| up == null || up.equals( ForgeDirection.UNKNOWN ) )
			return 0;

		int a = in.ordinal();
		int b = forward.ordinal();
		int c = up.ordinal();

		return OrientationMap[a][b][c];
	}

	public void setOriMap()
	{
		// pointed up...
		OrientationMap[0][3][1] = 0;
		OrientationMap[1][3][1] = 0;
		OrientationMap[2][3][1] = 0;
		OrientationMap[3][3][1] = 0;
		OrientationMap[4][3][1] = 0;
		OrientationMap[5][3][1] = 0;

		OrientationMap[0][5][1] = 1;
		OrientationMap[1][5][1] = 2;
		OrientationMap[2][5][1] = 0;
		OrientationMap[3][5][1] = 0;
		OrientationMap[4][5][1] = 0;
		OrientationMap[5][5][1] = 0;

		OrientationMap[0][2][1] = 3;
		OrientationMap[1][2][1] = 3;
		OrientationMap[2][2][1] = 0;
		OrientationMap[3][2][1] = 0;
		OrientationMap[4][2][1] = 0;
		OrientationMap[5][2][1] = 0;

		OrientationMap[0][4][1] = 2;
		OrientationMap[1][4][1] = 1;
		OrientationMap[2][4][1] = 0;
		OrientationMap[3][4][1] = 0;
		OrientationMap[4][4][1] = 0;
		OrientationMap[5][4][1] = 0;

		// upside down
		OrientationMap[0][3][0] = 0 | FLIP_H_BIT;
		OrientationMap[1][3][0] = 0 | FLIP_H_BIT;
		OrientationMap[2][3][0] = 3;
		OrientationMap[3][3][0] = 3;
		OrientationMap[4][3][0] = 3;
		OrientationMap[5][3][0] = 3;

		OrientationMap[0][4][0] = 2 | FLIP_H_BIT;
		OrientationMap[1][4][0] = 1 | FLIP_H_BIT;
		OrientationMap[2][4][0] = 3;
		OrientationMap[3][4][0] = 3;
		OrientationMap[4][4][0] = 3;
		OrientationMap[5][4][0] = 3;

		OrientationMap[0][5][0] = 1 | FLIP_H_BIT;
		OrientationMap[1][5][0] = 2 | FLIP_H_BIT;
		OrientationMap[2][5][0] = 3;
		OrientationMap[3][5][0] = 3;
		OrientationMap[4][5][0] = 3;
		OrientationMap[5][5][0] = 3;

		OrientationMap[0][2][0] = 3 | FLIP_H_BIT;
		OrientationMap[1][2][0] = 3 | FLIP_H_BIT;
		OrientationMap[2][2][0] = 3;
		OrientationMap[3][2][0] = 3;
		OrientationMap[4][2][0] = 3;
		OrientationMap[5][2][0] = 3;

		// side 1
		OrientationMap[0][3][5] = 1 | FLIP_V_BIT;
		OrientationMap[1][3][5] = 1 | FLIP_H_BIT;
		OrientationMap[2][3][5] = 1;
		OrientationMap[3][3][5] = 1;
		OrientationMap[4][3][5] = 1;
		OrientationMap[5][3][5] = 1 | FLIP_V_BIT;

		OrientationMap[0][1][5] = 1 | FLIP_H_BIT;
		OrientationMap[1][1][5] = 1;
		OrientationMap[2][1][5] = 3 | FLIP_V_BIT;
		OrientationMap[3][1][5] = 3;
		OrientationMap[4][1][5] = 1 | FLIP_V_BIT;
		OrientationMap[5][1][5] = 1;

		OrientationMap[0][2][5] = 1 | FLIP_H_BIT;
		OrientationMap[1][2][5] = 1 | FLIP_H_BIT;
		OrientationMap[2][2][5] = 1;
		OrientationMap[3][2][5] = 2 | FLIP_V_BIT;
		OrientationMap[4][2][5] = 1 | FLIP_V_BIT;
		OrientationMap[5][2][5] = 1;

		OrientationMap[0][0][5] = 1 | FLIP_H_BIT;
		OrientationMap[1][0][5] = 1;
		OrientationMap[2][0][5] = 0;
		OrientationMap[3][0][5] = 0 | FLIP_V_BIT;
		OrientationMap[4][0][5] = 1;
		OrientationMap[5][0][5] = 1 | FLIP_V_BIT;

		// side 2
		OrientationMap[0][1][2] = 0 | FLIP_H_BIT;
		OrientationMap[1][1][2] = 0;
		OrientationMap[2][1][2] = 2 | FLIP_H_BIT;
		OrientationMap[3][1][2] = 1;
		OrientationMap[4][1][2] = 3;
		OrientationMap[5][1][2] = 3 | FLIP_H_BIT;

		OrientationMap[0][4][2] = 0 | FLIP_H_BIT;
		OrientationMap[1][4][2] = 0 | FLIP_H_BIT;
		OrientationMap[2][4][2] = 2 | FLIP_H_BIT;
		OrientationMap[3][4][2] = 1;
		OrientationMap[4][4][2] = 1 | FLIP_H_BIT;
		OrientationMap[5][4][2] = 2;

		OrientationMap[0][0][2] = 0 | FLIP_V_BIT;
		OrientationMap[1][0][2] = 0;
		OrientationMap[2][0][2] = 2;
		OrientationMap[3][0][2] = 1 | FLIP_H_BIT;
		OrientationMap[4][0][2] = 3 | FLIP_H_BIT;
		OrientationMap[5][0][2] = 0;

		OrientationMap[0][5][2] = 0 | FLIP_H_BIT;
		OrientationMap[1][5][2] = 0 | FLIP_H_BIT;
		OrientationMap[2][5][2] = 2;
		OrientationMap[3][5][2] = 1 | FLIP_H_BIT;
		OrientationMap[4][5][2] = 2;
		OrientationMap[5][5][2] = 1 | FLIP_H_BIT;

		// side 3
		OrientationMap[0][0][3] = 3 | FLIP_H_BIT;
		OrientationMap[1][0][3] = 3;
		OrientationMap[2][0][3] = 1;
		OrientationMap[3][0][3] = 2 | FLIP_H_BIT;
		OrientationMap[4][0][3] = 0;
		OrientationMap[5][0][3] = 0 | FLIP_H_BIT;

		OrientationMap[0][4][3] = 3;
		OrientationMap[1][4][3] = 3;
		OrientationMap[2][4][3] = 1 | FLIP_H_BIT;
		OrientationMap[3][4][3] = 2;
		OrientationMap[4][4][3] = 1;
		OrientationMap[5][4][3] = 2 | FLIP_H_BIT;

		OrientationMap[0][1][3] = 3 | FLIP_V_BIT;
		OrientationMap[1][1][3] = 3;
		OrientationMap[2][1][3] = 1 | FLIP_H_BIT;
		OrientationMap[3][1][3] = 2;
		OrientationMap[4][1][3] = 3 | FLIP_H_BIT;
		OrientationMap[5][1][3] = 0;

		OrientationMap[0][5][3] = 3;
		OrientationMap[1][5][3] = 3;
		OrientationMap[2][5][3] = 1;
		OrientationMap[3][5][3] = 2 | FLIP_H_BIT;
		OrientationMap[4][5][3] = 2 | FLIP_H_BIT;
		OrientationMap[5][5][3] = 1;

		// side 4
		OrientationMap[0][3][4] = 1;
		OrientationMap[1][3][4] = 2;
		OrientationMap[2][3][4] = 2 | FLIP_H_BIT;
		OrientationMap[3][3][4] = 1;
		OrientationMap[4][3][4] = 2 | FLIP_H_BIT;
		OrientationMap[5][3][4] = 1;

		OrientationMap[0][0][4] = 1 | FLIP_H_BIT;
		OrientationMap[1][0][4] = 2;
		OrientationMap[2][0][4] = 0;
		OrientationMap[3][0][4] = 0 | FLIP_H_BIT;
		OrientationMap[4][0][4] = 2 | FLIP_H_BIT;
		OrientationMap[5][0][4] = 1;

		OrientationMap[0][1][4] = 1 | FLIP_H_BIT;
		OrientationMap[1][1][4] = 2;
		OrientationMap[2][1][4] = 3 | FLIP_H_BIT;
		OrientationMap[3][1][4] = 3;
		OrientationMap[4][1][4] = 2;
		OrientationMap[5][1][4] = 1 | FLIP_H_BIT;

		OrientationMap[0][2][4] = 1;
		OrientationMap[1][2][4] = 2;
		OrientationMap[2][2][4] = 1;
		OrientationMap[3][2][4] = 2 | FLIP_H_BIT;
		OrientationMap[4][2][4] = 2;
		OrientationMap[5][2][4] = 1 | FLIP_H_BIT;
	}

	public IOrientable getOrientable(Block block, IBlockAccess w, int x, int y, int z)
	{
		TileEntity te = w.getTileEntity( x, y, z );

		if ( te instanceof IOrientable )
			return (IOrientable) te;
		else
			return null;
	}

	public void preRenderInWorld(Block block, IBlockAccess world, int x, int y, int z, RenderBlocks renderer)
	{
		ForgeDirection forward = ForgeDirection.SOUTH;
		ForgeDirection up = ForgeDirection.UP;

		BlockRenderInfo info = getRendererInstance( block );
		IOrientable te = getOrientable( block, world, x, y, z );
		if ( te != null )
		{
			forward = te.getForward();
			up = te.getUp();

			renderer.uvRotateBottom = info.getTexture( ForgeDirection.DOWN ).setFlip( getOrientation( ForgeDirection.DOWN, forward, up ) );
			renderer.uvRotateTop = info.getTexture( ForgeDirection.UP ).setFlip( getOrientation( ForgeDirection.UP, forward, up ) );

			renderer.uvRotateEast = info.getTexture( ForgeDirection.EAST ).setFlip( getOrientation( ForgeDirection.EAST, forward, up ) );
			renderer.uvRotateWest = info.getTexture( ForgeDirection.WEST ).setFlip( getOrientation( ForgeDirection.WEST, forward, up ) );

			renderer.uvRotateNorth = info.getTexture( ForgeDirection.NORTH ).setFlip( getOrientation( ForgeDirection.NORTH, forward, up ) );
			renderer.uvRotateSouth = info.getTexture( ForgeDirection.SOUTH ).setFlip( getOrientation( ForgeDirection.SOUTH, forward, up ) );
		}

	}

	public void postRenderInWorld(RenderBlocks renderer)
	{
		renderer.uvRotateBottom = renderer.uvRotateEast = renderer.uvRotateNorth = renderer.uvRotateSouth = renderer.uvRotateTop = renderer.uvRotateWest = 0;
	}

	public boolean renderInWorld(Block block, IBlockAccess world, int x, int y, int z, RenderBlocks renderer)
	{
		BlockRenderInfo info = getRendererInstance( block );

		TileEntity te = world.getTileEntity( x, y, z );
		if ( te instanceof TileRotatableBlock )
		{
			TileRotatableBlock tr = (TileRotatableBlock) te;

			Block blk = tr.getBlock();
			int meta = tr.getMeta();

			if ( blk == null || !blk.isOpaqueCube() )
				return true;

			info.setTemporaryRenderIcons( blk.getIcon( ForgeDirection.UP.ordinal(), meta ), blk.getIcon( ForgeDirection.DOWN.ordinal(), meta ),
					blk.getIcon( ForgeDirection.SOUTH.ordinal(), meta ), blk.getIcon( ForgeDirection.NORTH.ordinal(), meta ),
					blk.getIcon( ForgeDirection.EAST.ordinal(), meta ), blk.getIcon( ForgeDirection.WEST.ordinal(), meta ) );
			// nTopIcon, nBottomIcon, nSouthIcon, nNorthIcon, nEastIcon, nWestIcon );

			preRenderInWorld( block, world, x, y, z, renderer );

			boolean o = renderer.renderStandardBlock( block, x, y, z );

			postRenderInWorld( renderer );
			return o;
		}
		else
			return renderer.renderStandardBlock( block, x, y, z );
	}

	final FloatBuffer rotMat = BufferUtils.createFloatBuffer( 16 );

	protected void applyTESRRotation(double x, double y, double z, ForgeDirection forward, ForgeDirection up)
	{
		if ( forward != null && up != null )
		{
			if ( forward == ForgeDirection.UNKNOWN )
				forward = ForgeDirection.SOUTH;

			if ( up == ForgeDirection.UNKNOWN )
				up = ForgeDirection.UP;

			ForgeDirection west = Platform.crossProduct( forward, up );

			rotMat.put( 0, west.offsetX );
			rotMat.put( 1, west.offsetY );
			rotMat.put( 2, west.offsetZ );
			rotMat.put( 3, 0 );

			rotMat.put( 4, up.offsetX );
			rotMat.put( 5, up.offsetY );
			rotMat.put( 6, up.offsetZ );
			rotMat.put( 7, 0 );

			rotMat.put( 8, forward.offsetX );
			rotMat.put( 9, forward.offsetY );
			rotMat.put( 10, forward.offsetZ );
			rotMat.put( 11, 0 );

			rotMat.put( 12, 0 );
			rotMat.put( 13, 0 );
			rotMat.put( 14, 0 );
			rotMat.put( 15, 1 );
			GL11.glTranslated( x + 0.5, y + 0.5, z + 0.5 );
			GL11.glMultMatrix( rotMat );
			GL11.glTranslated( -0.5, -0.5, -0.5 );
			GL11.glCullFace( GL11.GL_FRONT );
		}
		else
		{
			GL11.glTranslated( x, y, z );
		}
	}

	protected void setInvRenderBounds(RenderBlocks renderer, int i, int j, int k, int l, int m, int n)
	{
		renderer.setRenderBounds( i / 16.0, j / 16.0, k / 16.0, l / 16.0, m / 16.0, n / 16.0 );
	}

	protected void renderBlockBounds(RenderBlocks renderer,

	double minX, double minY, double minZ,

	double maxX, double maxY, double maxZ,

	ForgeDirection x, ForgeDirection y, ForgeDirection z)
	{
		minX /= 16.0;
		minY /= 16.0;
		minZ /= 16.0;
		maxX /= 16.0;
		maxY /= 16.0;
		maxZ /= 16.0;

		double aX = minX * x.offsetX + minY * y.offsetX + minZ * z.offsetX;
		double aY = minX * x.offsetY + minY * y.offsetY + minZ * z.offsetY;
		double aZ = minX * x.offsetZ + minY * y.offsetZ + minZ * z.offsetZ;

		double bX = maxX * x.offsetX + maxY * y.offsetX + maxZ * z.offsetX;
		double bY = maxX * x.offsetY + maxY * y.offsetY + maxZ * z.offsetY;
		double bZ = maxX * x.offsetZ + maxY * y.offsetZ + maxZ * z.offsetZ;

		if ( x.offsetX + y.offsetX + z.offsetX < 0 )
		{
			aX += 1;
			bX += 1;
		}

		if ( x.offsetY + y.offsetY + z.offsetY < 0 )
		{
			aY += 1;
			bY += 1;
		}

		if ( x.offsetZ + y.offsetZ + z.offsetZ < 0 )
		{
			aZ += 1;
			bZ += 1;
		}

		renderer.renderMinX = Math.min( aX, bX );
		renderer.renderMinY = Math.min( aY, bY );
		renderer.renderMinZ = Math.min( aZ, bZ );
		renderer.renderMaxX = Math.max( aX, bX );
		renderer.renderMaxY = Math.max( aY, bY );
		renderer.renderMaxZ = Math.max( aZ, bZ );
	}

	@SideOnly(Side.CLIENT)
	private void renderFace(Tessellator tess, double offsetX, double offsetY, double offsetZ, double ax, double ay, double az, double bx, double by, double bz,
			double ua, double ub, double va, double vb, IIcon ico, boolean flip)
	{
		if ( flip )
		{
			tess.addVertexWithUV( offsetX + ax * ua + bx * va, offsetY + ay * ua + by * va, offsetZ + az * ua + bz * va, ico.getInterpolatedU( ua * 16.0 ),
					ico.getInterpolatedV( va * 16.0 ) );
			tess.addVertexWithUV( offsetX + ax * ua + bx * vb, offsetY + ay * ua + by * vb, offsetZ + az * ua + bz * vb, ico.getInterpolatedU( ua * 16.0 ),
					ico.getInterpolatedV( vb * 16.0 ) );
			tess.addVertexWithUV( offsetX + ax * ub + bx * vb, offsetY + ay * ub + by * vb, offsetZ + az * ub + bz * vb, ico.getInterpolatedU( ub * 16.0 ),
					ico.getInterpolatedV( vb * 16.0 ) );
			tess.addVertexWithUV( offsetX + ax * ub + bx * va, offsetY + ay * ub + by * va, offsetZ + az * ub + bz * va, ico.getInterpolatedU( ub * 16.0 ),
					ico.getInterpolatedV( va * 16.0 ) );
		}
		else
		{
			tess.addVertexWithUV( offsetX + ax * ua + bx * va, offsetY + ay * ua + by * va, offsetZ + az * ua + bz * va, ico.getInterpolatedU( ua * 16.0 ),
					ico.getInterpolatedV( va * 16.0 ) );
			tess.addVertexWithUV( offsetX + ax * ub + bx * va, offsetY + ay * ub + by * va, offsetZ + az * ub + bz * va, ico.getInterpolatedU( ub * 16.0 ),
					ico.getInterpolatedV( va * 16.0 ) );
			tess.addVertexWithUV( offsetX + ax * ub + bx * vb, offsetY + ay * ub + by * vb, offsetZ + az * ub + bz * vb, ico.getInterpolatedU( ub * 16.0 ),
					ico.getInterpolatedV( vb * 16.0 ) );
			tess.addVertexWithUV( offsetX + ax * ua + bx * vb, offsetY + ay * ua + by * vb, offsetZ + az * ua + bz * vb, ico.getInterpolatedU( ua * 16.0 ),
					ico.getInterpolatedV( vb * 16.0 ) );
		}
	}

	@SideOnly(Side.CLIENT)
	protected void renderFace(int x, int y, int z, Block block, IIcon ico, RenderBlocks renderer, ForgeDirection orientation)
	{
		switch (orientation)
		{
		case NORTH:
			renderer.renderFaceZNeg( block, x, y, z, ico );
			break;
		case SOUTH:
			renderer.renderFaceZPos( block, x, y, z, ico );
			break;
		case EAST:
			renderer.renderFaceXPos( block, x, y, z, ico );
			break;
		case WEST:
			renderer.renderFaceXNeg( block, x, y, z, ico );
			break;
		case UP:
			renderer.renderFaceYPos( block, x, y, z, ico );
			break;
		case DOWN:
			renderer.renderFaceYNeg( block, x, y, z, ico );
			break;
		default:
			break;
		}
	}

	public void selectFace(RenderBlocks renderer, ForgeDirection west, ForgeDirection up, ForgeDirection forward, int u1, int u2, int v1, int v2)
	{
		v1 = 16 - v1;
		v2 = 16 - v2;

		double minX = (forward.offsetX > 0 ? 1 : 0) + mapFaceUV( west.offsetX, u1 ) + mapFaceUV( up.offsetX, v1 );
		double minY = (forward.offsetY > 0 ? 1 : 0) + mapFaceUV( west.offsetY, u1 ) + mapFaceUV( up.offsetY, v1 );
		double minZ = (forward.offsetZ > 0 ? 1 : 0) + mapFaceUV( west.offsetZ, u1 ) + mapFaceUV( up.offsetZ, v1 );

		double maxX = (forward.offsetX > 0 ? 1 : 0) + mapFaceUV( west.offsetX, u2 ) + mapFaceUV( up.offsetX, v2 );
		double maxY = (forward.offsetY > 0 ? 1 : 0) + mapFaceUV( west.offsetY, u2 ) + mapFaceUV( up.offsetY, v2 );
		double maxZ = (forward.offsetZ > 0 ? 1 : 0) + mapFaceUV( west.offsetZ, u2 ) + mapFaceUV( up.offsetZ, v2 );

		renderer.renderMinX = Math.max( 0.0, Math.min( minX, maxX ) - 0.001 );
		renderer.renderMaxX = Math.min( 1.0, Math.max( minX, maxX ) + 0.001 );

		renderer.renderMinY = Math.max( 0.0, Math.min( minY, maxY ) - 0.001 );
		renderer.renderMaxY = Math.min( 1.0, Math.max( minY, maxY ) + 0.001 );

		renderer.renderMinZ = Math.max( 0.0, Math.min( minZ, maxZ ) - 0.001 );
		renderer.renderMaxZ = Math.min( 1.0, Math.max( minZ, maxZ ) + 0.001 );
	}

	private double mapFaceUV(int offset, int uv)
	{
		if ( offset == 0 )
			return 0;

		if ( offset > 0 )
			return (double) uv / 16.0;

		return (16.0 - (double) uv) / 16.0;
	}

	public void renderTile(Block block, TileRotatableBlock tile, Tessellator tess, double x, double y, double z, float f, RenderBlocks renderer)
	{
		ForgeDirection forward = ForgeDirection.SOUTH;
		ForgeDirection up = ForgeDirection.UP;

		renderer.uvRotateBottom = renderer.uvRotateTop = renderer.uvRotateEast = renderer.uvRotateWest = renderer.uvRotateNorth = renderer.uvRotateSouth = 0;

		applyTESRRotation( x, y, z, forward, up );

		Minecraft.getMinecraft().getTextureManager().bindTexture( TextureMap.locationBlocksTexture );
		RenderHelper.disableStandardItemLighting();

		if ( Minecraft.isAmbientOcclusionEnabled() )
			GL11.glShadeModel( GL11.GL_SMOOTH );
		else
			GL11.glShadeModel( GL11.GL_FLAT );

		GL11.glColor4f( 1.0f, 1.0f, 1.0f, 1.0f );

		tess.setTranslation( -tile.xCoord, -tile.yCoord, -tile.zCoord );
		tess.startDrawingQuads();

		// note that this is a terrible approach...
		renderer.setRenderBoundsFromBlock( block );
		renderer.renderStandardBlock( block, tile.xCoord, tile.yCoord, tile.zCoord );

		tess.draw();
		tess.setTranslation( 0, 0, 0 );
		RenderHelper.enableStandardItemLighting();

		renderer.uvRotateBottom = renderer.uvRotateTop = renderer.uvRotateEast = renderer.uvRotateWest = renderer.uvRotateNorth = renderer.uvRotateSouth = 0;
	}

}
