close all;

for i=1:6
    d = data(i).hr;
    d = d(d(:,end)>0, :);
    figure;
    X = d(:,1);
    Y = d(:,4);
    Y = my_smooth(Y, 0.9);
    plot(X, Y)    
end