import chisel3._
import chisel3.util._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class Interface extends Bundle {
    val in_data  = Input(UInt(6.W))
    val valid    = Output(Bool())
    val out_data = Output(UInt(6.W))
}

class MS_interface extends Bundle {
    val s2m  = Input(UInt(6.W))
    val m2s  = Output(UInt(6.W))
}

class Top_module extends Module {
    val io = IO(new Interface)
    
    val master = Module(new Master)
    val slave  = Module(new Slave)

    io <> master.io.top_int     //connecting top with master => same direction and same name connects
    master.io.MS <> slave.io    //connecting master with slave => opposite direction and same name connects    
}

class Master extends Module {
    val io = IO(new Bundle {
      val top_int = new Interface
      val MS = new MS_interface
      
    })
    
   io.MS.m2s := io.top_int.in_data
   io.top_int.valid := true.B
   io.top_int.out_data := io.MS.s2m + io.MS.m2s
}

class Slave extends Module {
    val io = IO(Flipped(new MS_interface))
  
  io.s2m := io.m2s + 16.U  
}

println(chisel3.Driver.emitVerilog(new Top_module))
