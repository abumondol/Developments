
offset = 5;
for s=5:12
    d = data(s).data;
    a = data(s).annots;
    a = a(a(:,3)<=7, :);
    annot_count = size(a, 1);
    Rz = d(:, 2:4);
    len = length(Rz)
    
    for i = 10:annot_count
        ix = a(i,1)
        t = a(i,2);
        label = a(i,3);
        if i == 5
            break
        end
        
        close all;
        figure('units','normalized','outerposition',[0 0 1 1]);
        %figure

        d1 = d(ix-250:ix+250, :);            
        subplot(2,1,1)       % add first plot in 2 x 1 grid

        ttl = strcat('sub:', num2str(s), ', annot no:', num2str(i), ', time:', num2str(t), ', label:', num2str(label) );                    
        x = [-250:250];
        y = d1(:,2:4);        
        plot(x, y);
        legend('X','Y','Z');
        title(ttl);
        hold on
        grid on
        plot([x(1);x(end)], [0; 0], 'k'); %plot zero line                        
        scatter(0, 0, 'rd'); % annotated mark point
        
        [left, right] = get_angular_boundary(Rz, ix, 30);
        scatter(right-ix, 0, 'rx'); % annotated mark point
        scatter(left-ix, 0, 'rx'); % annotated mark point
        fprintf('%d, %d\n', right-ix, left-ix);
        
        subplot(2,1,2)       % add second plot in 2 x 1 grid            
        y = d1(:,8:10);        
        plot(x, y);
        legend('X','Y','Z');                        
        hold on
        grid on
        plot([x(1);x(end)], [0; 0], 'k'); %plot zero line                        
        scatter(0, 0, 'rd'); % annotated mark point
        
        waitforbuttonpress
        %waitforbuttonpress
    end
    
end