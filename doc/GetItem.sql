USE [wsp]
GO

/****** Object:  StoredProcedure [dbo].[GetItem]    Script Date: 2021/4/4 9:32:45 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
ALTER PROCEDURE [dbo].[GetItem] 
	@itemId int
,	@categ nvarchar(20)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT
      [item_name]
      ,[item_date]
      ,[descr]
      ,[valueN1] value1
      ,[valueD1] value2
      ,convert(nvarchar(30), coalesce(dttm2, dttm), 126) dttmstr
      ,[more]
	  ,[item_id]
	FROM [dbo].[MiscItems]
	WHERE item_id = IIF(@itemId = 0, item_id, @itemId)
	  and categ = @Categ
	  and ([doc_id] is null or [doc_id] > 0);
END

GO

