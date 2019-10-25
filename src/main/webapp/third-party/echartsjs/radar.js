window.onload = function(){
  let bg= document.getElementsByClassName('vulnerability-type-bg')

  let cos = Math.cos
  let sin = Math.sin

  let fontSize = 16

  let width = bg[0].clientWidth
  let height = bg[0].clientHeight

  // 中心点坐标
  let x = width / 2
  let y = height / 2

  // 颜色个数
  let colorLength = 5

  // 数据条数
  let dataLen = 9

  // 开始角度
  let startAngle = -Math.PI / 2

  // 半径
  let r = height / 5 * 2

  let cData = []
  let tData = []

  let tween = null
  let animationFrameId = null


  // 数据之间夹角
  let perAngle = Math.PI * 2 / dataLen

  let bgSvg = d3.select('.vulnerability-type-bg')
  let axisSvg = d3.select('.vulnerability-type-axis')
  let areaSvg = d3.select('.vulnerability-type-area')
  let labelSvg = d3.select('.vulnerability-type-label')

  var getData = function () {
    new Promise (function (resolve, reject) {
      let names = '1/2/3/4/5/6/7/8/9'.split('/')
      let results = names.map(name => {
        return {
          name,
          value: +Math.random().toFixed(1)
        }
      })
      resolve({
        data: results
      })
    }).then(function (res) {
      let data = res.data
      cData = data
      drawData()
      drawLabel()
    })
  }

  var drawBg = function () {
    bgSvg.selectAll('g').remove()

    // 画背景
    bgSvg.append('g')
        .selectAll('path')
        .data([...Array(colorLength).keys()])
        .enter()
        .append('path')
        .attr('d', function (i) {
          let d = ''
          let currentRadius = r * (colorLength - i) / colorLength
          for (let i = 0; i < dataLen; i++) {
            let currentAngle = startAngle + perAngle * i
            d += `${i === 0 ? 'M' : 'L'}${x + currentRadius * cos(currentAngle)},${y + currentRadius * sin(currentAngle)}`
          }
          return d + 'Z'
        })
        .attr('fill', i => i === 0 ? 'rgba(48, 99, 228, 0.35)' : 'rgba(0, 0, 0, 0.2)')
        .attr('stroke', i => i === 0 ? 'rgba(48, 99, 228, 0.7)' : 'none')
        .attr('stroke-width', 1)

    // 画分割线
    bgSvg.append('g')
         .selectAll('path')
         .data([...Array(dataLen).keys()])
         .enter()
         .append('path')
         .attr('d', function (i) {
          let currentAngle = startAngle + perAngle * i
          return `M${x},${y}L${x + r * cos(currentAngle)},${y + r * sin(currentAngle)}`
         })
         .attr('stroke', 'rgba(0, 0, 0, 0.4)')
         .attr('stroke-width', 1)
  }

  var drawAxis = function () {
    axisSvg.selectAll('g').remove()

    // let g = axisSvg.append('g')
    let g = axisSvg.selectAll('g')
              .data(['0.00', '0.25', '0.50', '0.75', '1.00'])
              .enter()
              .append('g')
    console.log(g)
    g.append('text')
      .attr('text-anchor', 'end')
      .attr('x', function () {
        return x - 15
      })
      .attr('y', function (d, i) {
        return y - r * (i + 1) / colorLength + fontSize / 2
      })
      .style('font-size', '16px')
      .style('fill', '#56d4f2')
      .text(d => d)
  }

  var drawData = function () {
    let start = {}
    let end = {}
    for (let i = 0; i < dataLen; i++) {
      if (tData[i]) {
        start[i] = tData[i].value
      } else {
        start[i] = 0
      }
      end[i] = cData[i].value || 0
    }

    tween = new TWEEN.Tween(start)
        .to(end, 1000)
        .onUpdate(function () {
          update(this)
        })
        .start()
        .onComplete(() => {
          tween = null
        })
    tData = cData
  }

  var update = function (obj) {
    let arr = []
    for (let i = 0; i < dataLen; i++) {
      arr.push(obj[i])
    }
    areaSvg.selectAll('g').remove()
    areaSvg.append('g')
         .append('path')
         .attr('d', function () {
           let d = ''
           for (let i = 0; i < arr.length; i++) {
            let currentAngle = startAngle + perAngle * i
            let ra = (r / 5) +  (r * 4 / 5 * arr[i])
            d += `${i === 0 ? 'M' : 'L'}${x + ra * cos(currentAngle)},${y + ra * sin(currentAngle)}`
           }
           return d + 'Z'
         })
         .attr('fill', 'rgba(106, 227, 235, 0.2)')
         .attr('stroke', 'rgb(106, 227, 235)')
         .attr('stroke-width', 1)
    drawCircle()
  }

  var drawLabel = function () {
    let names = cData.map(function (val) {
      return val.name
    })
    labelSvg.selectAll('g').remove()
    labelSvg.selectAll('text')
            .data(names, d => d)
            .enter()
            .append('text')
            .attr('x', function (d, i) {
              let currentAngle = startAngle + perAngle * i
              return x + (r + fontSize) * cos(currentAngle)
            })
            .attr('y', function (d, i) {
              let currentAngle = startAngle + perAngle * i
              if (i !==0) {
                return y + fontSize / 2 +(r + fontSize) *sin(currentAngle) + perAngle
              }
              else {
                return y + (r + fontSize) *sin(currentAngle) + perAngle
              }
            })
            .attr('text-anchor', function (d, i) {
              if (i === 0) {
                return 'middle'
              } else if (Math.floor(dataLen / 2) > i > 0) {
                return 'start'
              } else if (i >= Math.ceil(dataLen / 2)) {
                return 'end'
              }
            })
            .text(d => d)
            .style('fill', '#56d4f2')
  }

  var drawCircle = function () {
    let arr = cData.map(function (val) {
      return val.value
    })
    let g = areaSvg.select('g').selectAll('g')
                   .data(arr)
                   .enter()
                   .append('g')
                   .attr('opacity', 0)
                   .on('mouseover', function () {
                    d3.select(this).attr('opacity', 1)
                   })
                   .on('mouseout', function () {
                    d3.select(this).attr('opacity', 0)
                   })

    g.append('circle')
     .attr('cx', function (d, i) {
        let currentAngle = startAngle + perAngle * i
        let ra = (r / 5) +  (r * 4 / 5 * cData[i].value)
        return x + ra * cos(currentAngle)
     })
     .attr('cy', function (d, i) {
        let currentAngle = startAngle + perAngle * i
        let ra = (r / 5) +  (r * 4 / 5 * cData[i].value)
        return y + ra * sin(currentAngle)
     })
     .attr('r', 4)
     .attr('fill', 'rgb(106, 227, 235)')

     g.append('text')
      .attr('x', function (d, i) {
        let currentAngle = startAngle + perAngle * i
        let ra = (r / 5) +  (r * 4 / 5 * cData[i].value)
        return x + ra * cos(currentAngle) + 10
     })
      .attr('y', function (d, i) {
        let currentAngle = startAngle + perAngle * i
        let ra = (r / 5) +  (r * 4 / 5 * cData[i].value)
        return y + ra * sin(currentAngle) + 4
      })
      .attr('fill', 'rgb(106, 227, 235)')
      .text(function (d, i) {
        return d
      })

  }
  var btn = document.getElementById('button')
  btn.onclick = function(){
    getData()
  }

  var startAnimate = function () {
    animationFrameId = window.requestAnimationFrame(startAnimate)
    TWEEN.update()
  }
  startAnimate()
  getData()
  drawBg()
  drawAxis()
  drawCircle()
}

