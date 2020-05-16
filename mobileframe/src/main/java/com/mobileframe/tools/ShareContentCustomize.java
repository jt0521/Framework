package com.mobileframe.tools;

/**
 * 使用shareSdk分享，自定义分享内容设置。因为一键分享中，平台之间字段不兼容
 */
public class ShareContentCustomize
//		implements ShareContentCustomizeCallback
{
//	private String title;
//	private String content;
//	private String url;
//	private String imagePath = Mty_Application.MTYFilePath + "SHARE/mty_share.png";
//	private String imageUrl;
//	public ShareContentCustomize(String title, String content, String url, String imageUrl)
//	{
//		this.title = title;
//		this.content = content;
//		this.url = url;
//		this.imageUrl = imageUrl;
//	}
//	@Override
//	public void onShare(Platform platform, ShareParams paramsToShare)
//	{
//		//qq空间必带参数 	title 	titleUrl 	text 	site 	siteUrl
//		if (platform.getName().equals(QZone.NAME))
//		{
//			paramsToShare.setText(content);
//			paramsToShare.setTitle(title);
//			paramsToShare.setTitleUrl(url);
//			paramsToShare.setSite("美天游");
//			if (imageUrl != null && !imageUrl.equals(""))
//			{
//				paramsToShare.setImageUrl(imageUrl);
//			}
//			else
//			{
//				paramsToShare.setImagePath(imagePath);
//			}
//
//
//			paramsToShare.setSiteUrl(url);
//		}
//		//QQ
//		if (platform.getName().equals(QQ.NAME))
//		{
//			paramsToShare.setText(content);
//			paramsToShare.setTitle(title);
//			paramsToShare.setTitleUrl(url);
//			if (imageUrl != null && !imageUrl.equals(""))
//			{
//				paramsToShare.setImageUrl(imageUrl);
//			}
//			else
//			{
//				paramsToShare.setImagePath(imagePath);
//			}
//
////			shareQQ(context, text);
////			return;
//		}
//		//微信朋友圈可以分享单张图片或者图片与文字一起分享，微信好友可以进行文字或者单张图片或者文件进行分享（text的内容写入title才可以）
//		if (platform.getName().equals(Wechat.NAME))
//		{
//			paramsToShare.setText(content);
//			paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//			paramsToShare.setTitle(title);
//			if (imageUrl != null && !imageUrl.equals(""))
//			{
//				paramsToShare.setImageUrl(imageUrl);
//			}
//			else
//			{
//				paramsToShare.setImagePath(imagePath);
//			}
//
//			paramsToShare.setUrl(url);
//		}
//		if (platform.getName().equals(WechatMoments.NAME))
//		{
//			paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//			paramsToShare.setTitle(content);
//			if (imageUrl != null && !imageUrl.equals(""))
//			{
//				paramsToShare.setImageUrl(imageUrl);
//			}
//			else
//			{
//				paramsToShare.setImagePath(imagePath);
//			}
//
//			paramsToShare.setUrl(url);
//		}
//		//新浪微博
//		if (platform.getName().equals(SinaWeibo.NAME))
//		{
//			paramsToShare.setText(content + " " + url);
////			if (imageUrl != null && !imageUrl.equals(""))
////			{
////				paramsToShare.setImageUrl(imageUrl);
////			}
////			else
////			{
//				paramsToShare.setImagePath(imagePath);
////			}
//
//			paramsToShare.setUrl(url);
//		}
//
//
//	}
////	 public void shareQQ(Context mContext, String content){
////		    Intent sendIntent = new Intent();
////		        sendIntent.setAction(Intent.ACTION_SEND);
////		        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
////		        sendIntent.setType("text/plain");
////		        //sendIntent.setPackage("com.tencent.mobileqq");
////		        // List<ResolveInfo> list= getShareTargets(mContext);
////		        try {
////		            sendIntent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
////		            Intent chooserIntent = Intent.createChooser(sendIntent, "选择分享途径");
////		            if (chooserIntent == null) {
////		                return;
////		            }
////		            mContext.startActivity(chooserIntent);
////		        } catch (Exception e) {
////		            mContext.startActivity(sendIntent);
////		        }
////		    }
}
