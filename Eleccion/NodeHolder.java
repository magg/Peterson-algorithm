package Eleccion;

/**
* Eleccion/NodeHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Eleccion.idl
* Wednesday, November 30, 2011 4:46:21 PM CST
*/

public final class NodeHolder implements org.omg.CORBA.portable.Streamable
{
  public Eleccion.Node value = null;

  public NodeHolder ()
  {
  }

  public NodeHolder (Eleccion.Node initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = Eleccion.NodeHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    Eleccion.NodeHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return Eleccion.NodeHelper.type ();
  }

}
