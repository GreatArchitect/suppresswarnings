// pages/canvas/canvas.js
Page({
  data: {
    whoami : "wx.request.result"
  },
  clickName() {
    var that = this;
    wx.request({
      url: 'https://suppresswarnings.com/wx.http?action=miniprogram',
      success: function (res) {
        console.log(res.data)
        that.setData({ whoami: res.data });
      },
      fail: function (res) {
        console.log(res.data)
        that.setData({ whoami: that.data.whoami + '-fail-' + res });
      },
      complete: function (res) {
        console.log(res.data)
        that.setData({ whoami: that.data.whoami + '-complete-' + res });
      }
    })
    
  },
  onLoad: function (options) {
    var that = this;
    that.sys();
    console.log("hello world");
    const ctx = wx.createCanvasContext('canvas');
    ctx.setFillStyle('red');
    ctx.fillRect(20, 120, 50, 50);
    
    ctx.setFillStyle('green');
    ctx.fillRect(80, 120, 150, 50);

    ctx.setFillStyle('blue');
    ctx.fillRect(240, 120, 30, 50);

    ctx.setFillStyle('yellow');
    ctx.fillRect(20, 180, 250, 50);

    ctx.draw();
  },
  sys: function () {
    var that = this;
    wx.getSystemInfo({
      success: function (res) {
        that.setData({
          windowW: res.windowWidth,
          windowH: res.windowHeight
        })
      },
    })
  }
});