function plot_data_annot_points(acl, acl_annots)
    st = 36000;
    et = st+6000;
    a = acl(st:et,:);
    figure;
    plot(a);
    yl = 2000;
    ylim([-yl,yl]);
    legend('X','Y','Z');
    hold on    
    
    mp = find(acl_annots);
    mp = mp(mp>=st & mp<=et);
    mp = mp-st;
    count = length(mp);
    for i=1:count        
        x1 = mp(i);
        plot([x1;x1], [-yl; yl], 'b');
    end
    
    plot([0;et-st], [400; 400], 'k');

end