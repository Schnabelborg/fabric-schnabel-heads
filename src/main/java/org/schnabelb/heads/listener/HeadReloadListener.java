package org.schnabelb.heads.listener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.schnabelb.heads.HeadsMod;

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
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler,
			Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
		HeadsMod.loadItemData();
		return synchronizer.whenPrepared(null);
	}

	@Override
	public void reload(ResourceManager manager) {
		HeadsMod.loadItemData();
	}


}
