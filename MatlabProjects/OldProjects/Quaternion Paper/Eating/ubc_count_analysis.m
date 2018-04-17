r = zeros(12, 4);
for i = 1:12
    d = data(i).data;
    d = d(end,1) - d(1,1);
    a = data(i).annots;
    bt = a(a(:,3)<=7, 1);
    dr = a(a(:,3)>7, 1);
    
    r(i,:) = [d, length(a), length(bt), length(dr)];
    
end
format long g
min(r)
max(r)
mean(r)
sum(r)