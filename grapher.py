
import matplotlib.pyplot as plt
import itertools



xlabel = "time(s)"
ylabel = ["time(s)", "Transactions per(s)", "Chain Length","Total Jobs Done","Wrong Masters Caught","Wrong Masters Passed","Wrong Jobs Caught","Wrong Jobs Passed","Bad Hashers Caught","Bad Hashers Passed","Bad Workers Caught","Bad Workers Passed","Bad Blocks Passed","Bad Blocks Made","Difficulty"] 


def read_nums(line):
    nums = []
    r = ""
    i = 0
    while(i < len(line)):
        if(line[i].isdigit()):
            while(i < len(line) and (line[i].isdigit() or line[i] == ".")):
                r = str(r) + str(line[i])
                i = i + 1
            nums.append(r)
            r = ""
        i = i + 1

    return nums

            

def graph_line(index, lines, smoothed):  
    xdata = []
    ydata = []
    for i in range(len(lines)):
        xdata.append(float(lines[i][0]))
        ydata.append(float(lines[i][index]))
        plt.ylim(0, max(ydata)*1.1)
    if(smoothed):    
        plt.plot(xdata, moving_average(ydata))
    else:
        plt.plot(xdata, ydata)

    
    plt.xlabel(xlabel)
    plt.ylabel(ylabel[index])
    name = ylabel[index] + " vs " + xlabel
    plt.title(name)
    plt.savefig(name)
    plt.clf()
    plt.close()
   


def moving_average(data):
    res = 100
    r = []
    for index in range(len(data)):
        start = max(0, index - res)
        stop = min(index + res, len(data))

        sum = 0
        num = 0
        for i in range(start, stop):
            sum = sum + float(data[i])
            num = num + 1.0
        r.append(sum/num)
    return r
   
    




file = open("log.txt","r")
lines = []
for line in file:
    lines.append(read_nums(line))

graph_line(1, lines, True)
graph_line(2, lines, False)
graph_line(3, lines, False)
graph_line(4, lines, False)
graph_line(5, lines, False)
graph_line(6, lines, False)
graph_line(7, lines, False)
graph_line(8, lines, False)
graph_line(9, lines, False)
graph_line(10, lines, False)
graph_line(11, lines, False)
graph_line(12, lines, False)
graph_line(13, lines, False)
graph_line(14, lines, False)



    
