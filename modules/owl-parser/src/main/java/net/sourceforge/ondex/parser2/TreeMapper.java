package net.sourceforge.ondex.parser2;

import java.util.function.Function;
import java.util.stream.Stream;

import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.utils.ONDEXElemWrapper;

/**
 * TODO: comment me!
 *
 * @author brandizi
 * <dl><dt>Date:</dt><dd>30 May 2017</dd></dl>
 *
 */
public abstract class TreeMapper<S, SI> implements StreamMapper<ONDEXElemWrapper<S>, ONDEXConcept>
{
	private ConceptClassMapper<SI> conceptClassMapper;
	private ConceptMapper<SI> conceptMapper;
	private RelationMapper relationMapper;
	private boolean isMappingDirectionUp = true;
	private boolean doMapRootsToConcepts = true;
	
	private Scanner<S, SI> rootsScanner;
	private Scanner<SI, SI> childrenScanner;
	
	
	@Override
	public Stream<ONDEXConcept> map ( ONDEXElemWrapper<S> source )
	{
		ConceptClassMapper<SI> ccMapper = this.getConceptClassMapper ();
		final ConceptMapper<SI> conceptMapper = this.getConceptMapper ();
		
		@SuppressWarnings ( "unchecked" )
		Stream<SI>[] result = new Stream[] { Stream.empty () };
		
		ONDEXGraph graph = source.getGraph ();
		
		this.getRootsScanner ()
		.scan ( source.getElement () )
		.forEach ( root -> result [ 0 ] = Stream.concat ( result [ 0 ], scanTree ( ONDEXElemWrapper.of ( root, graph ) ) ) );
		
		// More TODO: doMapRootsToConcepts flag
		// TODO: deal with the wrappers hell! RelationMapper receives too many stupid wrappers!
			
		return result [ 0 ].map ( si -> conceptMapper.map ( si, ccMapper, graph ) );
	}

	protected Stream<SI> scanTree ( ONDEXElemWrapper<SI> rootSourceItem )
	{
		ConceptClassMapper<SI> ccmapper = this.getConceptClassMapper ();
		final ConceptMapper<SI> conceptMapper = this.getConceptMapper ();
		ONDEXConcept rootConcept = conceptMapper.map ( rootSourceItem, ccmapper );

		@SuppressWarnings ( "unchecked" )
		Stream<SI>[] result = new Stream[] { Stream.empty () };
				
		this.getChildrenScanner ()
		.scan ( rootSourceItem.getElement () )
		.forEach ( child -> 
		{
			ONDEXElemWrapper<ONDEXConcept> wchildConcept = conceptMapper.map ( child );
			if ( isMappingDirectionUp )
				relationMapper.map ( wchildConcept, wrootConcept );
			else
				relationMapper.map ( wrootConcept, wchildConcept );
		
			result [ 0 ] = Stream.concat ( result [ 0 ], scanTree ( child ) );
		});
		
		return result [ 0 ];
	}
	
	
	public ConceptClassMapper<SI> getConceptClassMapper ()
	{
		return conceptClassMapper;
	}

	public void setConceptClassMapper ( ConceptClassMapper<SI> conceptClassMapper )
	{
		this.conceptClassMapper = conceptClassMapper;
	}

	public ConceptMapper<SI> getConceptMapper ()
	{
		return conceptMapper;
	}

	public void setConceptMapper ( ConceptMapper<SI> conceptMapper )
	{
		this.conceptMapper = conceptMapper;
	}

	public RelationMapper getRelationMapper ()
	{
		return relationMapper;
	}

	public void setRelationMapper ( RelationMapper relationMapper )
	{
		this.relationMapper = relationMapper;
	}

	public boolean isMappingDirectionUp ()
	{
		return isMappingDirectionUp;
	}

	public void setMappingDirectionUp ( boolean isMappingDirectionUp )
	{
		this.isMappingDirectionUp = isMappingDirectionUp;
	}

	public boolean isDoMapRootsToConcepts ()
	{
		return doMapRootsToConcepts;
	}

	public void setDoMapRootsToConcepts ( boolean doMapRootsToConcepts )
	{
		this.doMapRootsToConcepts = doMapRootsToConcepts;
	}

	public Scanner<S, SI> getRootsScanner ()
	{
		return rootsScanner;
	}

	public void setRootsScanner ( Scanner<S, SI> rootsScanner )
	{
		this.rootsScanner = rootsScanner;
	}

	public Scanner<SI, SI> getChildrenScanner ()
	{
		return childrenScanner;
	}

	public void setChildrenScanner ( Scanner<SI, SI> childrenScanner )
	{
		this.childrenScanner = childrenScanner;
	}

	
}
