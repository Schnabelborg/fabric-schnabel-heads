package org.schnabelb.heads;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class HeadReloadListener implements SimpleSynchronousResourceReloadListener {

	@Override
	public Identifier getFabricId() {
		return new Identifier(HeadsMod.MODID, "head_reload_listener");
	}

	@Override
	public void reload(ResourceManager manager) {
		System.out.println("LEMAORO");
		HeadsMod.loadItemData();
	}

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler,
			Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
		return null;
	}


}
