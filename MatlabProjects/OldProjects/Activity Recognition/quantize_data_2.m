function data = quantize_data_2(data, factor)
d= round(grav*factor);
d=data().grav(:,1:2); %X-Y axis

[rows, cols] = size(grav);
res = zeros(rows, 6);

j=1;
lastVal = d(1,:);
count = 1;
start = 1;
for i=2:rows
    if d(i,1) == lastVal(1,1) && d(i,2) == lastVal(1,2)
        count = count+1;
    else
        res(j,1:4) = [lastVal, count, start];
        start = i;
        j = j+1;
        lastVal = d(i,:);
        count = 1;
    end
end
res(j,1:4) = [lastVal, count, start];
res = res(1:j, :);

for i=2:j
    lastX = res(i-1,1);
    lastY = res(i-1,2);
    curX = res(i,1);
    curY = res(i,2);
    
    if curX < lastX  && curY > lastY 
        res(i,5) = 1;
    elseif curX == lastX  && curY > lastY 
        res(i,5) = 2;
    elseif curX > lastX  && curY > lastY 
        res(i,5) = 3;
    elseif curX > lastX  && curY == lastY 
        res(i,5) = 4;
    elseif curX > lastX  && curY < lastY 
        res(i,5) = 5;
    elseif curX == lastX  && curY < lastY 
        res(i,5) = 6;
    elseif curX < lastX  && curY < lastY 
        res(i,5) = 7;
    elseif curX < lastX  && curY == lastY 
        res(i,5) = 8;
    else
        res(i,5) = 0;
    end
end

cond = res(:,5)==1 | res(:,5)==2 | res(:,5)==8;
res(cond,6) = 1;

cond = res(:,5)==4 | res(:,5)==5 | res(:,5)==6;
res(cond,6) = -1;

cond = res(:,5)==3 | res(:,5)==7;
res(cond,6) = 0;

end