import { ButtonHTMLAttributes, SelectHTMLAttributes, OptionHTMLAttributes, InputHTMLAttributes, HTMLAttributes, DetailedHTMLProps } from 'react';

type CoralIcon =
	| '123'
	| 'abc'
	| 'actions'
	| 'add'
	| 'addChildPanel'
	| 'addCircle'
	| 'adDisplay'
	| 'addPanelToolbar'
	| 'addToSelection'
	| 'adjust'
	| 'adobe'
	| 'adobeAdvertisingCloud'
	| 'adobeAnalytics'
	| 'adobeAnalyticsCloud'
	| 'adobeAudienceManager'
	| 'adobeCampaign'
	| 'adobeConnect'
	| 'adobeDocumentCloud'
	| 'adobeExperienceCloud'
	| 'adobeExperienceManager'
	| 'adobeMarketingCloud'
	| 'adobeMediaOptimizer'
	| 'adobePrimetime'
	| 'adobeSendNow'
	| 'adobeSign'
	| 'adobeSocial'
	| 'adobeTarget'
	| 'adPrint'
	| 'alert'
	| 'alertAdd'
	| 'alertCheck'
	| 'alertCircle'
	| 'alias'
	| 'amazonWebServices'
	| 'amcDataSource'
	| 'anchor'
	| 'android'
	| 'annotate'
	| 'answer'
	| 'answerFavorite'
	| 'app'
	| 'apple'
	| 'appRefresh'
	| 'approveReject'
	| 'apps'
	| 'archive'
	| 'archiveRemove'
	| 'arrowDown'
	| 'arrowLeft'
	| 'arrowRight'
	| 'arrowUp'
	| 'article'
	| 'asset'
	| 'assetCheck'
	| 'assetsAdded'
	| 'assetsDownloaded'
	| 'assetsExpired'
	| 'assetsLinkedPublished'
	| 'assetsModified'
	| 'assetsPublished'
	| 'asterisk'
	| 'at'
	| 'attach'
	| 'attachmentExclude'
	| 'automatedSegment'
	| 'back'
	| 'back30Seconds'
	| 'beaker'
	| 'beakerCheck'
	| 'beakerShare'
	| 'behance'
	| 'bell'
	| 'bidRule'
	| 'bidRuleAdd'
	| 'bitly'
	| 'blackberry'
	| 'blog'
	| 'blur'
	| 'book'
	| 'bookIndex'
	| 'bookmark'
	| 'boolean'
	| 'border'
	| 'box'
	| 'boxAdd'
	| 'boxExport'
	| 'boxImport'
	| 'brackets'
	| 'bracketsSquare'
	| 'branch1'
	| 'branch2'
	| 'branch3'
	| 'branchCircle'
	| 'breadcrumbNavigation'
	| 'breakdown'
	| 'breakdownAdd'
	| 'briefcase'
	| 'browse'
	| 'brush'
	| 'bug'
	| 'building'
	| 'bulkEditUsers'
	| 'button'
	| 'calculator'
	| 'calendar'
	| 'calendarAdd'
	| 'calendarLocked'
	| 'calendarUnlocked'
	| 'callCenter'
	| 'camera'
	| 'cameraRefresh'
	| 'campaign'
	| 'campaignAdd'
	| 'campaignClose'
	| 'campaignDelete'
	| 'campaignEdit'
	| 'captcha'
	| 'card'
	| 'channel'
	| 'chat'
	| 'check'
	| 'checkCircle'
	| 'checkmarkCircle'
	| 'checkPause'
	| 'chevronDoubleLeft'
	| 'chevronDoubleRight'
	| 'chevronDown'
	| 'chevronLeft'
	| 'chevronRight'
	| 'chevronUp'
	| 'chevronUpDown'
	| 'chrome'
	| 'circle'
	| 'clock'
	| 'clockCheck'
	| 'close'
	| 'closeCircle'
	| 'closedCaptions'
	| 'cloud'
	| 'cloudOutline'
	| 'code'
	| 'collection'
	| 'collectionAdd'
	| 'collectionAddTo'
	| 'collectionCheck'
	| 'collectionEdit'
	| 'collectionExclude'
	| 'collectionLink'
	| 'colorPalette'
	| 'columnAdd'
	| 'columnSettings'
	| 'columnTwoA'
	| 'columnTwoB'
	| 'columnTwoC'
	| 'comment'
	| 'commentAdd'
	| 'compare'
	| 'compass'
	| 'confidenceFour'
	| 'confidenceOne'
	| 'confidenceThree'
	| 'confidenceTwo'
	| 'conversionFunnel'
	| 'copy'
	| 'coverImage'
	| 'creativeCloud'
	| 'creditCard'
	| 'crop'
	| 'cropLightning'
	| 'crosshairs'
	| 'curate'
	| 'cursorArrow'
	| 'cut'
	| 'dashboard'
	| 'data'
	| 'dataAdd'
	| 'dataAdobe'
	| 'dataBook'
	| 'dataCheck'
	| 'dataCorrelated'
	| 'dataDownload'
	| 'dataEdit'
	| 'dataRefresh'
	| 'dataRemove'
	| 'dataUnavailable'
	| 'dataUpload'
	| 'dataUser'
	| 'date'
	| 'dateInput'
	| 'deduplication'
	| 'delegate'
	| 'delete'
	| 'deleteOutline'
	| 'deliveryFusion'
	| 'demographic'
	| 'desktopAndMobile'
	| 'detach'
	| 'deviceDesktop'
	| 'deviceLaptop'
	| 'devicePhone'
	| 'devicePhoneRefresh'
	| 'devicePreview'
	| 'deviceRotateLandscape'
	| 'deviceRotatePortrait'
	| 'devices'
	| 'deviceTablet'
	| 'deviceTV'
	| 'dimension'
	| 'disqus'
	| 'divide'
	| 'documentFragment'
	| 'documentFragmentGroup'
	| 'dolly'
	| 'download'
	| 'downloaded'
	| 'downloadFromCloud'
	| 'dps'
	| 'draft'
	| 'dragHandle'
	| 'dropdown'
	| 'duplicate'
	| 'edit'
	| 'editCircle'
	| 'editExclude'
	| 'education'
	| 'effects'
	| 'efficient'
	| 'email'
	| 'emailAlert'
	| 'emailCancel'
	| 'emailCheck'
	| 'emailExclude'
	| 'emailGear'
	| 'emailKey'
	| 'emailLightning'
	| 'emailNotification'
	| 'emailRefresh'
	| 'emailSchedule'
	| 'engagement'
	| 'event'
	| 'eventExclude'
	| 'eventShare'
	| 'exclude'
	| 'experience'
	| 'experienceAdd'
	| 'experienceAddTo'
	| 'experienceExport'
	| 'experienceImport'
	| 'export'
	| 'extension'
	| 'eyedropper'
	| 'facebook'
	| 'facebookCoverImage'
	| 'fast'
	| 'fastForward'
	| 'fastForwardCircle'
	| 'feed'
	| 'feedAdd'
	| 'feedManagement'
	| 'file'
	| 'fileAdd'
	| 'fileCampaign'
	| 'fileChart'
	| 'fileCode'
	| 'fileCSV'
	| 'fileData'
	| 'fileEmail'
	| 'fileExcel'
	| 'fileFolder'
	| 'fileGear'
	| 'fileGlobe'
	| 'fileGlobe2'
	| 'fileHTML'
	| 'fileIdea'
	| 'fileImportant'
	| 'fileIndex'
	| 'fileJson'
	| 'fileKey'
	| 'fileMobile'
	| 'filePDF'
	| 'fileShare'
	| 'fileSingleWebPage'
	| 'fileSpace'
	| 'fileTask'
	| 'fileTemplate'
	| 'fileTxt'
	| 'fileUser'
	| 'fileWord'
	| 'fileWorkflow'
	| 'fileXML'
	| 'fileZip'
	| 'filingCabinet'
	| 'film'
	| 'filter'
	| 'filterAdd'
	| 'filterCheck'
	| 'filterDelete'
	| 'filterEdit'
	| 'filterHeart'
	| 'filterRemove'
	| 'filterStar'
	| 'findAndReplace'
	| 'firefox'
	| 'flag'
	| 'flagExclude'
	| 'flash'
	| 'flashlight'
	| 'flashlightOff'
	| 'flashlightOn'
	| 'flickr'
	| 'folder'
	| 'folderAdd'
	| 'folderAddTo'
	| 'folderAdobe'
	| 'folderArchive'
	| 'folderContent'
	| 'folderDelete'
	| 'folderGear'
	| 'folderLocked'
	| 'folderLockedAdobe'
	| 'folderOutline'
	| 'folderRemove'
	| 'folderSearch'
	| 'folderUser'
	| 'follow'
	| 'followOff'
	| 'forecast'
	| 'form'
	| 'forPlacementOnly'
	| 'forward'
	| 'fragmentAdd'
	| 'fragmentExpand'
	| 'fullScreen'
	| 'fullScreenExit'
	| 'game'
	| 'gauge1'
	| 'gauge2'
	| 'gauge3'
	| 'gauge4'
	| 'gauge5'
	| 'gdprLegalRequest'
	| 'gear'
	| 'gears'
	| 'gearsAdd'
	| 'gearsDelete'
	| 'gearsEdit'
	| 'genderFemale'
	| 'genderMale'
	| 'gift'
	| 'globe'
	| 'globeCheck'
	| 'globeClock'
	| 'globeEnter'
	| 'globeExit'
	| 'globeGrid'
	| 'globeRemove'
	| 'globeSearch'
	| 'globeStrike'
	| 'globeStrikeClock'
	| 'google'
	| 'googleAnalytics'
	| 'googlePlayStore'
	| 'googlePlus'
	| 'googlePlus1'
	| 'graphArea'
	| 'graphAreaStacked'
	| 'graphBarHorizontal'
	| 'graphBarHorizontalAdd'
	| 'graphBarHorizontalStacked'
	| 'graphBarVertical'
	| 'graphBarVerticalAdd'
	| 'graphBarVerticalStacked'
	| 'graphBubble'
	| 'graphBullet'
	| 'graphConfidenceBands'
	| 'graphDonut'
	| 'graphDonutAdd'
	| 'graphGantt'
	| 'graphHistogram'
	| 'graphPathing'
	| 'graphPie'
	| 'graphProfitCurve'
	| 'graphScatter'
	| 'graphStream'
	| 'graphStreamRanked'
	| 'graphStreamRankedAdd'
	| 'graphSunburst'
	| 'graphTree'
	| 'graphTrend'
	| 'graphTrendAdd'
	| 'graphTrendAlert'
	| 'group'
	| 'groupInPanel'
	| 'hammer'
	| 'hand'
	| 'hand0'
	| 'hand1'
	| 'hand2'
	| 'hand3'
	| 'hand4'
	| 'heart'
	| 'help'
	| 'helpCircle'
	| 'history'
	| 'home'
	| 'homepage'
	| 'html5'
	| 'image'
	| 'imageAdd'
	| 'imageAlbum'
	| 'imageCarousel'
	| 'imageCheck'
	| 'imageMapCircle'
	| 'imageMapPolygon'
	| 'imageMapRectangle'
	| 'imageNext'
	| 'imageProfile'
	| 'images'
	| 'imageSearch'
	| 'imageText'
	| 'import'
	| 'inbox'
	| 'individual'
	| 'info'
	| 'infoCircle'
	| 'instagram'
	| 'internetExplorer'
	| 'jumpToTop'
	| 'key'
	| 'keyboard'
	| 'keyClock'
	| 'keyExclude'
	| 'launch'
	| 'layers'
	| 'layersBackward'
	| 'layersBringToFront'
	| 'layersForward'
	| 'layersSendToBack'
	| 'lightbulb'
	| 'link'
	| 'linkCheck'
	| 'linkedIn'
	| 'linkFacebook'
	| 'linkGlobe'
	| 'linkNav'
	| 'linkOff'
	| 'linkOut'
	| 'linkOutLight'
	| 'linkPage'
	| 'linkUser'
	| 'linux'
	| 'location'
	| 'locationBasedDate'
	| 'locationBasedEvent'
	| 'locationContribution'
	| 'lockOff'
	| 'lockOn'
	| 'login'
	| 'logOut'
	| 'magentoCommerce'
	| 'mailbox'
	| 'marketingActivities'
	| 'mBox'
	| 'menu'
	| 'merge'
	| 'minus'
	| 'minusCircle'
	| 'mobileServices'
	| 'money'
	| 'moon'
	| 'more'
	| 'moreVertical'
	| 'move'
	| 'moveLeftRight'
	| 'moveUpDown'
	| 'multiple'
	| 'multipleAdd'
	| 'multipleCheck'
	| 'multipleExclude'
	| 'news'
	| 'newsAdd'
	| 'newsgator'
	| 'nielsen'
	| 'note'
	| 'noteAdd'
	| 'offer'
	| 'offerDelete'
	| 'onAir'
	| 'oneDrive'
	| 'open'
	| 'openIn'
	| 'openRecent'
	| 'opera'
	| 'orbit'
	| 'organisations'
	| 'organize'
	| 'os'
	| 'page'
	| 'pageBreak'
	| 'pageExclude'
	| 'pageGear'
	| 'pageRule'
	| 'pages'
	| 'pagesExclude'
	| 'pageShare'
	| 'pageTag'
	| 'paintBucket'
	| 'panel'
	| 'paste'
	| 'pasteHTML'
	| 'pasteList'
	| 'pasteText'
	| 'pause'
	| 'pauseCircle'
	| 'pausePlay'
	| 'pausePlayCircle'
	| 'pawn'
	| 'pending'
	| 'peopleGroup'
	| 'personalizationField'
	| 'phoneGap'
	| 'phoneGapBuilder'
	| 'pinOff'
	| 'pinOn'
	| 'pinterest'
	| 'pivot'
	| 'platformDataMapping'
	| 'play'
	| 'playCircle'
	| 'plug'
	| 'popIn'
	| 'popOut'
	| 'preview'
	| 'print'
	| 'printPreview'
	| 'project'
	| 'projectAdd'
	| 'promote'
	| 'properties'
	| 'public'
	| 'publish'
	| 'publishCheck'
	| 'publishPending'
	| 'publishReject'
	| 'publishRemove'
	| 'publishSchedule'
	| 'pushNotification'
	| 'question'
	| 'rail'
	| 'railBottom'
	| 'railLeft'
	| 'railRight'
	| 'railRightClose'
	| 'railRightOpen'
	| 'railTop'
	| 'redo'
	| 'reflectHorizontal'
	| 'reflectVertical'
	| 'refresh'
	| 'reorder'
	| 'replies'
	| 'reply'
	| 'replyAll'
	| 'report'
	| 'reportAdd'
	| 'resize'
	| 'retweet'
	| 'reuse'
	| 'revenue'
	| 'revert'
	| 'rewind'
	| 'rewindCircle'
	| 'ribbon'
	| 'rotateLeft'
	| 'rotateRight'
	| 'rss'
	| 'safari'
	| 'save'
	| 'saveAs'
	| 'saveTo'
	| 'scribble'
	| 'search'
	| 'seat'
	| 'seatAdd'
	| 'select'
	| 'selectAll'
	| 'selectContainer'
	| 'selectGear'
	| 'selectionChecked'
	| 'selectionDownload'
	| 'sendForSignature'
	| 'sentimentNegative'
	| 'sentimentNeutral'
	| 'sentimentPositive'
	| 'separator'
	| 'servers'
	| 'share'
	| 'shareCheck'
	| 'sharpen'
	| 'shield'
	| 'shoppingCart'
	| 'showMenu'
	| 'shuffle'
	| 'sinaWeibo'
	| 'slow'
	| 'sms'
	| 'smsKey'
	| 'smsLightning'
	| 'smsRefresh'
	| 'socialNetwork'
	| 'spam'
	| 'spellcheck'
	| 'spin'
	| 'sqlQuery'
	| 'stage'
	| 'stamp'
	| 'starburst'
	| 'starFill'
	| 'starStroke'
	| 'stock'
	| 'stop'
	| 'stopCircle'
	| 'stopwatch'
	| 'strokeWidth'
	| 'subscribe'
	| 'substractFromSelection'
	| 'successMetric'
	| 'summarize'
	| 'switch'
	| 'sync'
	| 'syncRemove'
	| 'table'
	| 'tableAdd'
	| 'tableAndChart'
	| 'tableau'
	| 'tableCellsMerge'
	| 'tableCellsSplit'
	| 'tableColumnAddLeft'
	| 'tableColumnAddRight'
	| 'tableColumnMerge'
	| 'tableColumnRemove'
	| 'tableColumnRemoveCenter'
	| 'tableColumnSplit'
	| 'tableEdit'
	| 'tableHistogram'
	| 'tableMergeCells'
	| 'tableRowAdd'
	| 'tableRowAddBottom'
	| 'tableRowAddTop'
	| 'tableRowMerge'
	| 'tableRowRemove'
	| 'tableRowRemoveCenter'
	| 'tableRowSplit'
	| 'tableSelectColumn'
	| 'tableSelectRow'
	| 'tag'
	| 'tagExclude'
	| 'tags'
	| 'target'
	| 'targeted'
	| 'taskList'
	| 'teapot'
	| 'testAB'
	| 'testABEdit'
	| 'testABGear'
	| 'testABRemove'
	| 'testProfile'
	| 'text'
	| 'textAdd'
	| 'textBold'
	| 'textBulleted'
	| 'textBulletedAttach'
	| 'textBulletedHierarchy'
	| 'textBulletedHierarchyExclude'
	| 'textCenter'
	| 'textColor'
	| 'textDecrease'
	| 'textEdit'
	| 'textExclude'
	| 'textIncrease'
	| 'textIndentDecrease'
	| 'textIndentIncrease'
	| 'textItalic'
	| 'textJustified'
	| 'textKerning'
	| 'textLeft'
	| 'textLetteredLowercase'
	| 'textLetteredUppercase'
	| 'textNumbered'
	| 'textParagraph'
	| 'textRight'
	| 'textRomanLowercase'
	| 'textRomanUppercase'
	| 'textSize'
	| 'textSizeAdd'
	| 'textSpaceAfter'
	| 'textSpaceBefore'
	| 'textStrikethrough'
	| 'textStyle'
	| 'textSubscript'
	| 'textSuperscript'
	| 'textUnderline'
	| 'thumbDown'
	| 'thumbUp'
	| 'transferToPlatform'
	| 'transparency'
	| 'trap'
	| 'treeCollapse'
	| 'treeCollapseAll'
	| 'treeExpand'
	| 'treeExpandAll'
	| 'trendInspect'
	| 'triangleDown'
	| 'triangleRight'
	| 'trophy'
	| 'tumblr'
	| 'twitter'
	| 'type'
	| 'undo'
	| 'ungroup'
	| 'unmerge'
	| 'upload'
	| 'usa'
	| 'user'
	| 'userActivity'
	| 'userAdd'
	| 'userAdmin'
	| 'userArrow'
	| 'userEdit'
	| 'userExclude'
	| 'userLock'
	| 'users'
	| 'usersAdd'
	| 'usersExclude'
	| 'userShare'
	| 'usersLock'
	| 'usersShare'
	| 'viewAllTags'
	| 'viewBiWeek'
	| 'viewCard'
	| 'viewColumn'
	| 'viewDay'
	| 'viewDetail'
	| 'viewedMarkAs'
	| 'viewGrid'
	| 'viewList'
	| 'viewOff'
	| 'viewOn'
	| 'viewRow'
	| 'viewSingle'
	| 'viewSOMExpression'
	| 'viewStack'
	| 'viewWeek'
	| 'visit'
	| 'visitShare'
	| 'vk'
	| 'volumeMute'
	| 'volumeOne'
	| 'volumeThree'
	| 'volumeTwo'
	| 'wand'
	| 'watch'
	| 'windows7'
	| 'windows8'
	| 'wordpress'
	| 'workflow'
	| 'workflowAdd'
	| 'wrench'
	| 'youTube'
	| 'zoomIn'
	| 'zoomOut';
type CoralIconSize = 'XS' | 'S' | 'M' | 'L';

declare module 'react' {
	interface ButtonHTMLAttributes<T> {
		icon?: CoralIcon;
		iconsize?: CoralIconSize;
		size?: 'M' | 'L';
		variant?: 'cta' | 'primary' | 'quiet' | 'warning' | 'minimal' | undefined;
	}
	namespace JSX {
		interface IntrinsicElements {
			'coral-select': DetailedHTMLProps<SelectHTMLAttributes<HTMLSelectElement> & { class?: string }, HTMLSelectElement>;
			'coral-select-item': DetailedHTMLProps<OptionHTMLAttributes<HTMLOptionElement> & { class?: string }, HTMLSelectElement>;
			'coral-checkbox': DetailedHTMLProps<InputHTMLAttributes<HTMLInputElement> & { class?: string }, HTMLInputElement>;
			'coral-checkbox-label': DetailedHTMLProps<HTMLAttributes<HTMLElement> & { class?: string }, HTMLElement>;
			'coral-switch': DetailedHTMLProps<InputHTMLAttributes<HTMLInputElement> & { class?: string }, HTMLInputElement>;
			'coral-wait': DetailedHTMLProps<HTMLAttributes<HTMLElement> & { class?: string }, HTMLElement>;
			'coral-icon': DetailedHTMLProps<
				HTMLAttributes<HTMLElement> & { icon: CoralIcon; size?: CoralIconSize; class?: string },
				HTMLElement
			>;
			'coral-selectlist': DetailedHTMLProps<SelectHTMLAttributes<HTMLSelectElement> & { class?: string }, HTMLSelectElement>;
			'coral-selectlist-item': DetailedHTMLProps<OptionHTMLAttributes<HTMLOptionElement> & { class?: string }, HTMLSelectElement>;
			'coral-popover': DetailedHTMLProps<
				HTMLAttributes<HTMLDialogElement> & {
					target?: string;
					placement?: 'top' | 'bottom' | 'left' | 'right';
					class?: string;
				},
				HTMLDialogElement
			>;
			'coral-popover-header': DetailedHTMLProps<HTMLAttributes<HTMLElement> & { class?: string }, HTMLElement>;
			'coral-popover-content': DetailedHTMLProps<HTMLAttributes<HTMLElement> & { class?: string }, HTMLElement>;
		}
	}
}
