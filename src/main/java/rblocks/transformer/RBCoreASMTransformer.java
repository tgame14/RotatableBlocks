package rblocks.transformer;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import rblocks.core.RBLog;
import rblocks.transformer.annotations.RBClientMethod;
import rblocks.transformer.annotations.RBCoreCopy;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.Side;

public class RBCoreASMTransformer implements IClassTransformer
{

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		try
		{
			if ( transformedName != null && transformedName.equals( "net.minecraft.client.renderer.RenderBlocks" ) )
			{
				log( "Found RenderBlocks." );

				ClassNode classNode = new ClassNode();
				ClassReader classReader = new ClassReader( basicClass );
				classReader.accept( classNode, 0 );

				for (MethodNode mn : classNode.methods)
				{
					// MD: ble/b (Lahu;III)Z net/minecraft/client/renderer/RenderBlocks/renderBlockByRenderType
					// (Lnet/minecraft/block/Block;III)Z

					boolean signatureMatch = mn.desc.equals( "(Lahu;III)Z" ) || mn.desc.equals( "(Lnet/minecraft/block/Block;III)Z" );
					boolean nameMatch = mn.name.equals( "renderBlockByRenderType" ) || mn.name.equals( "b" ) || mn.name.equals( "func_147805_b" );

					if ( nameMatch && signatureMatch )
					{
						log( " Found renderBlockByRenderType." );
						Iterator<AbstractInsnNode> i = mn.instructions.iterator();
						while (i.hasNext())
						{
							AbstractInsnNode node = i.next();
							if ( node instanceof MethodInsnNode )
							{
								log( " Replaced " + ((MethodInsnNode) node).name );
								((MethodInsnNode) node).name = "getRealRenderType";
								break;
							}
						}

					}
				}

				ClassWriter writer = new ClassWriter( ClassWriter.COMPUTE_MAXS );
				classNode.accept( writer );
				return writer.toByteArray();
			}

			if ( transformedName != null && transformedName.equals( "net.minecraft.block.Block" ) )
			{
				log( "Found Block." );

				ClassNode srcNode = new ClassNode();
				InputStream is = getClass().getResourceAsStream( "/rblocks/transformer/template/BlockChange.class" );
				ClassReader srcReader = new ClassReader( is );
				srcReader.accept( srcNode, 0 );

				ClassNode classNode = new ClassNode();
				ClassReader classReader = new ClassReader( basicClass );
				classReader.accept( classNode, 0 );

				for (FieldNode fn : srcNode.fields)
					classNode.fields.add( fn );

				classNode.interfaces.add( "rblocks/api/IRBMethods" );

				for (MethodNode mn : srcNode.methods)
				{
					if ( hasAnnotation( mn.visibleAnnotations, RBCoreCopy.class ) )
					{
						log( "Found " + mn.name );
						handleMethod( classNode, srcNode.name, mn );
					}
				}

				ClassWriter writer = new ClassWriter( ClassWriter.COMPUTE_MAXS );
				classNode.accept( writer );
				return writer.toByteArray();
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}

		return basicClass;
	}

	private void handleMethod(ClassNode classNode, String from, MethodNode mn)
	{
		if ( FMLLaunchHandler.side() == Side.SERVER && hasAnnotation( mn.visibleAnnotations, RBClientMethod.class ) )
		{
			RBLog.info( "Skipping, method client only." );
			return;
		}

		Iterator<AbstractInsnNode> i = mn.instructions.iterator();
		while (i.hasNext())
		{
			processNode( i.next(), from, classNode.name );
		}

		for (MethodNode tmn : classNode.methods)
		{
			if ( tmn.name.equals( mn.name ) && tmn.desc.equals( mn.desc ) )
			{
				RBLog.info( "Found " + tmn.name + " : Appending" );

				AbstractInsnNode finalReturn = mn.instructions.getLast();
				while (!isReturn( finalReturn.getOpcode() ))
				{
					mn.instructions.remove( finalReturn );
					finalReturn = mn.instructions.getLast();
				}
				mn.instructions.remove( finalReturn );

				tmn.instructions.insert( mn.instructions );
				return;
			}
		}

		RBLog.info( "No Such Method " + mn.name + " : Adding" );
		classNode.methods.add( mn );
	}

	private boolean hasAnnotation(List<AnnotationNode> anns, Class anno)
	{
		if ( anns == null )
			return false;

		for (AnnotationNode ann : anns)
		{
			if ( ann.desc.equals( Type.getDescriptor( anno ) ) )
			{
				return true;
			}
		}

		return false;
	}

	private boolean isReturn(int opcode)
	{
		switch (opcode)
		{
		case Opcodes.ARETURN:
		case Opcodes.DRETURN:
		case Opcodes.FRETURN:
		case Opcodes.LRETURN:
		case Opcodes.IRETURN:
		case Opcodes.RETURN:
			return true;
		}
		return false;
	}

	private void processNode(AbstractInsnNode next, String from, String nePar)
	{
		if ( next instanceof FieldInsnNode )
		{
			FieldInsnNode min = (FieldInsnNode) next;
			if ( min.owner.equals( from ) )
			{
				min.owner = nePar;
			}
		}
		if ( next instanceof MethodInsnNode )
		{
			MethodInsnNode min = (MethodInsnNode) next;
			if ( min.owner.equals( from ) )
			{
				min.owner = nePar;
			}
		}
	}

	private void log(String string)
	{
		FMLRelaunchLog.log( "RBCore-CORE", Level.INFO, string );
	}

}
