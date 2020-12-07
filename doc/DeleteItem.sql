USE [wsp]
GO

/****** Object:  StoredProcedure [dbo].[DeleteItem]    Script Date: 2021/4/7 18:45:03 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
ALTER PROCEDURE [dbo].[DeleteItem] 
	@item_id int
,	@categ nvarchar(20)
,	@row int OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	UPDATE [dbo].[MiscItems]
	   SET [doc_id] = -1001
	 WHERE [item_id] = @item_id and [categ] = @categ;

	 select @row = @@ROWCOUNT;

END


GO

