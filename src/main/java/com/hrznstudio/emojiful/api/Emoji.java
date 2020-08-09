package com.hrznstudio.emojiful.api;

import com.hrznstudio.emojiful.Emojiful;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class Emoji implements Predicate<String> {
    public static final Identifier loading_texture = new Identifier(Emojiful.MODID, "textures/26a0.png");
    public static final Identifier noSignal_texture = new Identifier(Emojiful.MODID, "textures/26d4.png");
    public static final Identifier error_texture = new Identifier(Emojiful.MODID, "textures/26d4.png");

    public static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);
    public String name;
    public List<String> strings;
    public String location;
    public int version = 1;

    public boolean deleteOldTexture;

    public DownloadImageData img;
    public Identifier resourceLocation = loading_texture;

    public void checkLoad() {
        if (img != null)
            return;

        img = new DownloadImageData(new File("emojiful/cache/" + name + "-" + version), "https://raw.githubusercontent.com/HrznStudio/Emojiful/master/" + location, loading_texture);
        resourceLocation = new Identifier(Emojiful.MODID, "texures/emoji/" + name.toLowerCase() + "_" + version);
        MinecraftClient.getInstance().getTextureManager().registerTexture(resourceLocation, img);
    }

    public Identifier getResourceLocationForBinding() {
        checkLoad();
        if (deleteOldTexture) {
            img.clearGlId();
            deleteOldTexture = false;
        }
        return img != null && img.textureUploaded ? resourceLocation : loading_texture;
    }

    @Override
    public boolean test(String s) {
        for (String text : strings)
            if (s.equalsIgnoreCase(text))
                return true;
        return false;
    }

    public class DownloadImageData extends ResourceTexture {
        private final File cacheFile;
        private final String imageUrl;
        private NativeImage nativeImage;
        private Thread imageThread;
        public boolean textureUploaded;

        public DownloadImageData(File cacheFileIn, String imageUrlIn, Identifier textureResourceLocation) {
            super(textureResourceLocation);
            this.cacheFile = cacheFileIn;
            this.imageUrl = imageUrlIn;
        }

        private void checkTextureUploaded() {
            if (!this.textureUploaded) {
                if (this.nativeImage != null) {
                    if (this.location != null) {
                        this.clearGlId();
                    }
                    TextureUtil.uploadImage(IntBuffer.allocate(super.getGlId()), this.nativeImage.getWidth(), this.nativeImage.getHeight());
                    this.nativeImage.upload(0, 0, 0, true);
                    this.textureUploaded = true;
                }
            }
        }

        private void setImage(NativeImage nativeImageIn) {
            MinecraftClient.getInstance().execute(() -> {
                this.textureUploaded = true;
                if (!RenderSystem.isOnRenderThread()) {
                    RenderSystem.recordRenderCall(() -> {
                        this.upload(nativeImageIn);
                    });
                } else {
                    this.upload(nativeImageIn);
                }

            });
        }

        private void upload(NativeImage imageIn) {
            TextureUtil.allocate(this.getGlId(), imageIn.getWidth(), imageIn.getHeight());
            imageIn.upload(0, 0, 0, true);
        }

        private NativeImage loadTexture(InputStream inputStreamIn) {
            NativeImage nativeimage = null;

            try {
                nativeimage = NativeImage.read(inputStreamIn);
            } catch (IOException ioexception) {
                Emojiful.LOGGER.warn("Error while loading the skin texture", (Throwable)ioexception);
            }

            return nativeimage;
        }

        @Override
        public void load(ResourceManager resourceManager) throws IOException {
            if (this.imageThread == null) {
                if (this.cacheFile != null && this.cacheFile.isFile()) {
                    try {
                        FileInputStream fileinputstream = new FileInputStream(this.cacheFile);
                        setImage(this.loadTexture(fileinputstream));
                    } catch (IOException ioexception) {
                        this.loadTextureFromServer();
                    }
                } else {
                    this.loadTextureFromServer();
                }
            }
        }

        protected void loadTextureFromServer() {
            this.imageThread = new Thread("Emojiful Texture Downloader #" + threadDownloadCounter.incrementAndGet()) {
                @Override
                public void run() {
                    HttpURLConnection httpurlconnection = null;

                    try {
                        httpurlconnection = (HttpURLConnection) (new URL(DownloadImageData.this.imageUrl)).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                        httpurlconnection.setDoInput(true);
                        httpurlconnection.setDoOutput(false);
                        httpurlconnection.connect();
                        if (httpurlconnection.getResponseCode() / 100 == 2) {
                            int contentLength = httpurlconnection.getContentLength();
                            InputStream inputStream;

                            if (DownloadImageData.this.cacheFile != null) {
                                FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), DownloadImageData.this.cacheFile);
                                inputStream = new FileInputStream(DownloadImageData.this.cacheFile);
                            } else {
                                inputStream = httpurlconnection.getInputStream();
                            }

                            DownloadImageData.this.setImage(DownloadImageData.this.loadTexture(inputStream));
                        } else {
                            Emoji.this.resourceLocation = noSignal_texture;
                            Emoji.this.deleteOldTexture = true;
                            DownloadImageData.this.textureUploaded = true;
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Emoji.this.resourceLocation = error_texture;
                        Emoji.this.deleteOldTexture = true;
                        DownloadImageData.this.textureUploaded = true;
                    } finally {
                        if (httpurlconnection != null) {
                            httpurlconnection.disconnect();
                        }
                    }
                }
            };
            this.imageThread.setDaemon(true);
            this.imageThread.start();
        }
    }
}
